package com.quickbite.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MenuItem extends BaseEntity {
    private String name;
    private String description;
    private BigDecimal price;
    private String category; 
    private boolean available;
    private int prepTimeMinutes;

    public MenuItem() {
        super();
        this.available = true;
    }

    public MenuItem(Long id, String name, String description, BigDecimal price,
                    String category, boolean available, int prepTimeMinutes, LocalDateTime createdAt) {
        super(id, createdAt);
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.available = available;
        this.prepTimeMinutes = prepTimeMinutes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getPrepTimeMinutes() {
        return prepTimeMinutes;
    }

    public void setPrepTimeMinutes(int prepTimeMinutes) {
        this.prepTimeMinutes = prepTimeMinutes;
    }
}