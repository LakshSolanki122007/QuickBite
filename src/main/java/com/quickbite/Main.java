package com.quickbite;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import com.quickbite.model.User;
import com.quickbite.util.PasswordUtil;
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
                path.equals("/api/health")) {
                return;
            }

            String authHeader = ctx.header("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer qb_sess_")) {
                ctx.status(401).json(Map.of("error", "Unauthorized: Please log in first."));
            }
        });

        // Root & Health
        app.get("/", ctx -> {
            ctx.result("QuickBite Canteen Management System Server is Running!");
        });

        app.get("/api/health", ctx -> {
            ctx.json(Map.of("status", "UP"));
        });

        // POST /api/auth/register - BCrypt hashed
        app.post("/api/auth/register", ctx -> {
            User user = ctx.bodyAsClass(User.class);
            if (user.getUsername() == null || user.getPassword() == null ||
                user.getUsername().isBlank() || user.getPassword().isBlank()) {
                ctx.status(400).json(Map.of("error", "Username and password cannot be empty."));
                return;
            }

            if (userDatabase.containsKey(user.getUsername())) {
                ctx.status(409).json(Map.of("error", "Username already exists."));
                return;
            }

            String hashedPassword = PasswordUtil.hashPassword(user.getPassword());
            user.setPassword(hashedPassword);
            user.setRole("STUDENT");

            userDatabase.put(user.getUsername(), user);

            ctx.status(201).json(Map.of(
                "message", "User registered successfully",
                "username", user.getUsername(),
                "role", user.getRole()
            ));
        });

        // POST /api/auth/login - BCrypt verification
        app.post("/api/auth/login", ctx -> {
            User credentials = ctx.bodyAsClass(User.class);
            User storedUser = userDatabase.get(credentials.getUsername());

            if (storedUser != null && PasswordUtil.checkPassword(credentials.getPassword(), storedUser.getPassword())) {
                ctx.json(Map.of(
                    "status", "success",
                    "username", storedUser.getUsername(),
                    "role", storedUser.getRole(),
                    "token", "qb_sess_" + UUID.randomUUID().toString()
                ));
            } else {
                ctx.status(401).json(Map.of("error", "Invalid username or password."));
            }
        });

        System.out.println("QuickBite Server successfully started on http://localhost:7000");
    }
}