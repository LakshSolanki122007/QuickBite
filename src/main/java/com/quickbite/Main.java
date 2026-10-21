package com.quickbite;

import com.quickbite.controller.OrderController;
import com.quickbite.model.User;
import com.quickbite.util.PasswordUtil;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    public static void main(String[] args) {

        // In-memory credential store backed by BCrypt
        Map<String, User> userDatabase = new ConcurrentHashMap<>();

        // Pre-seed default Staff/Admin credentials ("admin" / "admin123")
        userDatabase.put("admin", new User(
            "admin",
            PasswordUtil.hashPassword("admin123"),
            "STAFF"
        ));

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public", Location.CLASSPATH);
        }).start(8080);

        System.out.println("QuickBite Server successfully started on http://localhost:8080");

        // Security Filter: Enforce authentication on all protected API paths
        app.before("/api/*", ctx -> {
            String path = ctx.path();
            if (path.equals("/api/auth/login") ||
                path.equals("/api/auth/register") ||
                path.equals("/api/health") ||
                path.startsWith("/api/orders")) {
                return;
            }

            String authHeader = ctx.header("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer qb_sess_")) {
                ctx.status(401).json(Map.of("error", "Unauthorized: Please log in first."));
            }
        });

        // Root & Health Endpoints
        app.get("/", ctx -> ctx.redirect("/index.html"));
        app.get("/api/health", ctx -> ctx.json(Map.of("status", "UP", "message", "QuickBite server running")));

        // Authentication: Login
        app.post("/api/auth/login", ctx -> {
            Map<String, String> creds = ctx.bodyAsClass(Map.class);
            String username = creds.get("username");
            String password = creds.get("password");

            User user = userDatabase.get(username);
            if (user != null && PasswordUtil.checkPassword(password, user.getPassword())) {
                String token = "qb_sess_" + UUID.randomUUID().toString();
                ctx.json(Map.of(
                    "success", true,
                    "token", token,
                    "role", user.getRole(),
                    "username", username
                ));
            } else {
                ctx.status(401).json(Map.of("success", false, "error", "Invalid username or password."));
            }
        });

        // Authentication: Register
        app.post("/api/auth/register", ctx -> {
            Map<String, String> creds = ctx.bodyAsClass(Map.class);
            String username = creds.get("username");
            String password = creds.get("password");

            if (username == null || password == null || username.isBlank() || password.isBlank()) {
                ctx.status(400).json(Map.of("error", "Username and password are required."));
                return;
            }

            if (userDatabase.containsKey(username)) {
                ctx.status(409).json(Map.of("error", "Username already exists."));
                return;
            }

            User newUser = new User(username, PasswordUtil.hashPassword(password), "STUDENT");
            userDatabase.put(username, newUser);
            ctx.status(201).json(Map.of("success", true, "message", "Registration successful!"));
        });

        // Order Routes (Student checkout, polling, and Kitchen Admin status transitions)
        app.post("/api/orders", OrderController::createOrder);
        app.get("/api/orders", OrderController::getOrders);
        app.patch("/api/orders/{token}/status", OrderController::updateOrderStatus);
    }
}