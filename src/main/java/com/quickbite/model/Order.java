package com.quickbite.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order extends BaseEntity {
    private Long userId;
    private Long pickupSlotId;
    private String status; // "PENDING", "PREPARING", "READY", "COMPLETED", "CANCELLED"
    private BigDecimal totalPrice;

    public Order() {
        super();
        this.status = "PENDING";
        this.totalPrice = BigDecimal.ZERO;
    }

    public Order(Long id, Long userId, Long pickupSlotId, String status, BigDecimal totalPrice, LocalDateTime createdAt) {
        super(id, createdAt);
        this.userId = userId;
        this.pickupSlotId = pickupSlotId;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPickupSlotId() {
        return pickupSlotId;
    }

    public void setPickupSlotId(Long pickupSlotId) {
        this.pickupSlotId = pickupSlotId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}