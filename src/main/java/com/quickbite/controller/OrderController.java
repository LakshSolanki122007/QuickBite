package com.quickbite.controller;

import com.quickbite.dao.OrderDao;
import io.javalin.http.Context;
import java.util.Map;

public class OrderController {
    private static final OrderDao orderDao = new OrderDao();

    // Endpoint handler to place an order and return the global sequential token
    @SuppressWarnings("unchecked")
    public static void createOrder(Context ctx) {
        try {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            String slot = (String) body.getOrDefault("slot", "12:00 PM - 12:15 PM");
            double totalAmount = Double.parseDouble(body.getOrDefault("totalPrice", body.getOrDefault("total", "0.0")).toString());

            String token = orderDao.createOrder(slot, totalAmount);

            ctx.status(201).json(Map.of(
                "success", true,
                "token", token,
                "message", "Order placed successfully!"
            ));
        } catch (Exception e) {
            ctx.status(500).json(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    // Endpoint handler to fetch all orders for cross-device sync
    public static void getOrders(Context ctx) {
        try {
            ctx.json(orderDao.getAllOrders());
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }

    // Endpoint handler to update order status from the Admin Panel
    @SuppressWarnings("unchecked")
    public static void updateOrderStatus(Context ctx) {
        try {
            String token = ctx.pathParam("token");
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            String newStatus = (String) body.get("status");

            if (newStatus == null || newStatus.isBlank()) {
                ctx.status(400).json(Map.of("error", "Status field is required"));
                return;
            }

            boolean updated = orderDao.updateStatus(token, newStatus);

            if (updated) {
                ctx.status(200).json(Map.of(
                    "success", true,
                    "token", token,
                    "status", newStatus,
                    "message", "Order status updated successfully!"
                ));
            } else {
                ctx.status(404).json(Map.of("error", "Order not found with token: " + token));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }
}