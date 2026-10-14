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
            double totalAmount = Double.parseDouble(body.getOrDefault("total", "0.0").toString());

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
}