package com.quickbite.dao;

import com.quickbite.model.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class OrderDao {
    private static final List<Order> orders = Collections.synchronizedList(new ArrayList<>());
    private static final AtomicLong idCounter = new AtomicLong(101);

    /**
     * Creates an Order compatible with your Order.java schema.
     * Uses your existing 6-argument constructor:
     * Order(Long id, Long userId, Long pickupSlotId, String status, BigDecimal totalPrice, LocalDateTime createdAt)
     */
    public String createOrder(String slot, double totalAmount) {
        long orderId = idCounter.getAndIncrement();
        String token = "TK-" + orderId;

        Order order = new Order(
            orderId,
            1L,                                  // Default userId
            1L,                                  // Default pickupSlotId
            "PENDING",                           // Initial status
            BigDecimal.valueOf(totalAmount),     // totalPrice
            LocalDateTime.now()                  // createdAt
        );

        orders.add(order);
        return token;
    }

    /**
     * Returns a thread-safe list of active orders for the admin board.
     */
    public List<Order> getAllOrders() {
        synchronized (orders) {
            return new ArrayList<>(orders);
        }
    }

    /**
     * Finds the order by matching the token or numeric ID, then updates the status.
     */
    public boolean updateStatus(String token, String newStatus) {
        if (token == null || newStatus == null) {
            return false;
        }

        synchronized (orders) {
            for (Order order : orders) {
                String tokenWithPrefix = "TK-" + order.getId();
                String rawId = String.valueOf(order.getId());

                if (token.equalsIgnoreCase(tokenWithPrefix) || token.equalsIgnoreCase(rawId)) {
                    order.setStatus(newStatus.toUpperCase());
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Finds a single order by token or numeric ID.
     */
    public Order getOrderByToken(String token) {
        if (token == null) {
            return null;
        }

        synchronized (orders) {
            for (Order order : orders) {
                String tokenWithPrefix = "TK-" + order.getId();
                String rawId = String.valueOf(order.getId());

                if (token.equalsIgnoreCase(tokenWithPrefix) || token.equalsIgnoreCase(rawId)) {
                    return order;
                }
            }
        }
        return null;
    }
}