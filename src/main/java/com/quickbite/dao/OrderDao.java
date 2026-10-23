package com.quickbite.dao;

import com.quickbite.model.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.quickbite.util.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderDao {
    private static final List<Order> orders = Collections.synchronizedList(new ArrayList<>());
    private static final AtomicLong idCounter = new AtomicLong(101);
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    /**
     * Creates an Order compatible with your Order.java schema.
     * Uses your existing 6-argument constructor:
     * Order(Long id, Long userId, Long pickupSlotId, String status, BigDecimal totalPrice, LocalDateTime createdAt)
     */
    public String createOrder(String slot, double totalAmount) {
        long orderId = idCounter.getAndIncrement();
        String token = "TK-" + orderId;

        // 1. In-memory record (keeps UI polling fast)
        Order order = new Order(
            orderId,
            1L,                      // Default userId
            1L,                      // Default pickupSlotId
            "PENDING",               // Initial status
            BigDecimal.valueOf(totalAmount), // totalPrice
            LocalDateTime.now()      // createdAt
        );
        orders.add(order);

        // 2. Persist record to MySQL
        String sql = "INSERT INTO orders (student_id, slot_id, token_number, total_amount, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, 1L);
            stmt.setLong(2, 1L);
            stmt.setString(3, token);
            stmt.setBigDecimal(4, BigDecimal.valueOf(totalAmount));
            stmt.setString(5, "PENDING");
            stmt.executeUpdate();
            System.out.println("[DB] Order successfully persisted: " + token);
        } catch (SQLException e) {
            System.err.println("[DB Error] Failed to persist order: " + e.getMessage());
        }
// Auto-advance statuses every 3 seconds
        scheduler.schedule(() -> updateOrderStatus(token, orderId, "PREPARING"), 3, TimeUnit.SECONDS);
        scheduler.schedule(() -> updateOrderStatus(token, orderId, "READY"), 6, TimeUnit.SECONDS);
        scheduler.schedule(() -> updateOrderStatus(token, orderId, "COMPLETED"), 9, TimeUnit.SECONDS);
        return token;
    }
    public void updateOrderStatus(String token, long orderId, String newStatus) {
        // 1. Update in-memory cache for live polling
        synchronized (orders) {
            for (Order o : orders) {
                if (o.getId() != null && o.getId() == orderId) {
                    o.setStatus(newStatus);
                    break;
                }
            }
        }

        // 2. Persist updated status in MySQL
        String sql = "UPDATE orders SET status = ? WHERE token_number = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setString(2, token);
            stmt.executeUpdate();
            System.out.println("[Timer Sync] " + token + " -> " + newStatus);
        } catch (SQLException e) {
            System.err.println("[Timer DB Error] " + e.getMessage());
        }
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