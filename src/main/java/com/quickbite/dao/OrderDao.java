package com.quickbite.dao;

import com.quickbite.util.DatabaseUtil;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDao {

    // Method to create an order and generate a global sequential token
    public synchronized String createOrder(String slot, double totalAmount) {
        String insertQuery = "INSERT INTO orders (pickup_slot, total_amount, status, created_at) VALUES (?, ?, 'PREPARING', ?)";
        String tokenString = "#01";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, slot);
            pstmt.setDouble(2, totalAmount);
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long orderId = generatedKeys.getLong(1);
                        tokenString = String.format("#%02d", orderId);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating order in database: " + e.getMessage());
        }

        return tokenString;
    }

    // Method to fetch all orders for cross-device synchronization
    public List<Map<String, Object>> getAllOrders() {
        List<Map<String, Object>> orderList = new ArrayList<>();
        String query = "SELECT * FROM orders ORDER BY id DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Map<String, Object> order = new HashMap<>();
                order.put("id", "QB-" + rs.getLong("id"));
                order.put("token", String.format("#%02d", rs.getLong("id")));
                order.put("slot", rs.getString("pickup_slot"));
                order.put("total", rs.getDouble("total_amount"));
                order.put("status", rs.getString("status"));
                order.put("time", rs.getTimestamp("created_at").toLocalDateTime().toLocalTime().toString());
                orderList.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching orders from database: " + e.getMessage());
        }

        return orderList;
    }
}