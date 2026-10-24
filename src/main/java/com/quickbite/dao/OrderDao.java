package com.quickbite.dao;

import com.quickbite.model.Order;
import com.quickbite.util.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class OrderDao {
    private static final List<Order> orders = Collections.synchronizedList(new ArrayList<>());
    private static final AtomicLong idCounter = new AtomicLong(101);
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    // 1. Startup Hydration: Read all existing orders from MySQL on boot
    static {
        loadOrdersFromDatabase();
    }

    public static void loadOrdersFromDatabase() {
        String sql = "SELECT id, student_id, slot_id, token_number, total_amount, status, created_at FROM orders ORDER BY id ASC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            synchronized (orders) {
                orders.clear();
                long maxId = 100;
                while (rs.next()) {
                    long id = rs.getLong("id");
                    long studentId = rs.getLong("student_id");
                    long slotId = rs.getLong("slot_id");
                    String status = rs.getString("status");
                    BigDecimal totalAmount = rs.getBigDecimal("total_amount");
                    Timestamp ts = rs.getTimestamp("created_at");
                    LocalDateTime createdAt = (ts != null) ? ts.toLocalDateTime() : LocalDateTime.now();

                    orders.add(new Order(id, studentId, slotId, status, totalAmount, createdAt));
                    if (id > maxId) {
                        maxId = id;
                    }
                }
                idCounter.set(maxId + 1);
            }
            System.out.println("[DB Hydration] Loaded " + orders.size() + " orders from MySQL. Next Token ID: " + idCounter.get());
        } catch (SQLException e) {
            System.err.println("[DB Hydration Error] Could not load orders: " + e.getMessage());
        }
    }

    // 2. Order Placement: Insert new order into MySQL
    public String createOrder(String itemsSummary, double totalAmount) {
        long orderId = idCounter.getAndIncrement();
        String token = "TK-" + orderId;

        Order order = new Order(
            orderId,
            1L,
            1L,
            "PENDING",
            BigDecimal.valueOf(totalAmount),
            LocalDateTime.now()
        );
        orders.add(order);

        String sql = "INSERT INTO orders (id, student_id, slot_id, token_number, total_amount, status) VALUES (?, 1, 1, ?, ?, 'PENDING')";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, orderId);
            stmt.setString(2, token);
            stmt.setBigDecimal(3, BigDecimal.valueOf(totalAmount));
            stmt.executeUpdate();
            System.out.println("[DB Insert] Persisted " + token + " to MySQL.");
        } catch (SQLException e) {
            System.err.println("[DB Insert Error] " + e.getMessage());
        }

        // 3. Automated Status Progression: Update status in MySQL at 3s, 6s, 9s
        scheduler.schedule(() -> updateOrderStatus(token, orderId, "PREPARING"), 3, TimeUnit.SECONDS);
        scheduler.schedule(() -> updateOrderStatus(token, orderId, "READY"), 6, TimeUnit.SECONDS);
        scheduler.schedule(() -> updateOrderStatus(token, orderId, "COMPLETED"), 9, TimeUnit.SECONDS);

        return token;
    }

    // Automated transition helper
    public void updateOrderStatus(String token, long orderId, String newStatus) {
        synchronized (orders) {
            for (Order o : orders) {
                if (o.getId() != null && o.getId() == orderId) {
                    o.setStatus(newStatus);
                    break;
                }
            }
        }

        String sql = "UPDATE orders SET status = ? WHERE token_number = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setString(2, token);
            stmt.executeUpdate();
            System.out.println("[DB Update] " + token + " -> " + newStatus);
        } catch (SQLException e) {
            System.err.println("[DB Update Error] " + e.getMessage());
        }
    }

    // 4. Admin Manual Status Override: Update MySQL directly
    public boolean updateStatus(String token, String newStatus) {
        if (token == null || newStatus == null) return false;

        boolean updated = false;
        synchronized (orders) {
            for (Order o : orders) {
                String fullToken = "TK-" + o.getId();
                if (token.equalsIgnoreCase(fullToken) || token.equalsIgnoreCase(String.valueOf(o.getId()))) {
                    o.setStatus(newStatus);
                    updated = true;
                    break;
                }
            }
        }

        if (updated) {
            String sql = "UPDATE orders SET status = ? WHERE token_number = ? OR token_number = ?";
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newStatus);
                stmt.setString(2, token);
                stmt.setString(3, "TK-" + token);
                stmt.executeUpdate();
                System.out.println("[DB Admin Override] " + token + " -> " + newStatus);
            } catch (SQLException e) {
                System.err.println("[DB Admin Override Error] " + e.getMessage());
            }
        }
        return updated;
    }

    public List<Order> getAllOrders() {
        synchronized (orders) {
            return new ArrayList<>(orders);
        }
    }
}