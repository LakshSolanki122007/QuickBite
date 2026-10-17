package com.quickbite;

import com.quickbite.controller.OrderController;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

/**
 * Main application entry point for QuickBite.
 */
public class Main {
    public static void main(String[] args) {
        // Initialize Javalin server on port 7000
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/frontend", Location.CLASSPATH);
        }).start(7000);

        // Register complete CRUD API endpoints for orders
        app.post("/api/orders", OrderController::createOrder);       // Create
        app.get("/api/orders", OrderController::getOrders);          // Read
        app.put("/api/orders/{id}", OrderController::updateOrder);   // Update
        app.delete("/api/orders/{id}", OrderController::deleteOrder);// Delete

        System.out.println("QuickBite Server started successfully on http://localhost:7000");
    }
}