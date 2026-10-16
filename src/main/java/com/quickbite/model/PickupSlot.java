package com.quickbite.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class PickupSlot extends BaseEntity {
    private LocalTime startTime;
    private LocalTime endTime;
    private int maxCapacity;
    private boolean active;

    public PickupSlot() {
        super();
        this.active = true;
    }

    public PickupSlot(Long id, LocalTime startTime, LocalTime endTime, int maxCapacity, boolean active, LocalDateTime createdAt) {
        super(id, createdAt);
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxCapacity = maxCapacity;
        this.active = active;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}