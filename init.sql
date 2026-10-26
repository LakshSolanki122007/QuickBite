CREATE DATABASE IF NOT EXISTS quickbite_db;
USE quickbite_db;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_STUDENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS menu_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    description TEXT,
    price DECIMAL(8, 2) NOT NULL,
    category VARCHAR(50) NOT NULL, -- 'MEALS', 'SNACKS', 'BEVERAGES'
    is_available BOOLEAN DEFAULT TRUE,
    prep_time_minutes INT DEFAULT 10,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pickup_slots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    max_capacity INT NOT NULL DEFAULT 30,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    slot_id BIGINT NOT NULL,
    token_number VARCHAR(20) NOT NULL UNIQUE,
    total_amount DECIMAL(8, 2) NOT NULL DEFAULT 0.00,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- 'PENDING', 'PREPARING', 'READY', 'COMPLETED', 'CANCELLED'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_orders_slot FOREIGN KEY (slot_id) REFERENCES pickup_slots(id)
);

CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    menu_item_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(8, 2) NOT NULL,
    CONSTRAINT fk_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_items_menu FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)
);

-- Seed default users (Required so fk_orders_student does not fail when placing an order)
INSERT IGNORE INTO users (id, name, email, password_hash, role) VALUES
(1, 'Demo Student', 'student@quickbite.com', 'demo_student_hash', 'ROLE_STUDENT'),
(2, 'Kitchen Admin', 'admin@quickbite.com', 'demo_admin_hash', 'ROLE_ADMIN');

-- Sample pickup time slots for lunch break
INSERT IGNORE INTO pickup_slots (id, start_time, end_time, max_capacity, is_active) VALUES
(1, '12:45:00', '13:00:00', 25, TRUE),
(2, '13:00:00', '13:15:00', 35, TRUE),
(3, '13:15:00', '13:30:00', 35, TRUE),
(4, '13:30:00', '13:45:00', 20, TRUE);

-- Sample menu items
INSERT IGNORE INTO menu_items (id, name, description, price, category, is_available, prep_time_minutes) VALUES
(1, 'Veg Grilled Sandwich', 'Toasted bread with fresh veggies and mint chutney', 60.00, 'SNACKS', TRUE, 8),
(2, 'Paneer Kathi Roll', 'Spiced paneer wrapped in flaky paratha', 80.00, 'SNACKS', TRUE, 10),
(3, 'Mini Thali Meal', 'Dal, seasonal sabzi, 3 rotis, rice, and pickle', 110.00, 'MEALS', TRUE, 12),
(4, 'Cold Coffee', 'Chilled blended brew with chocolate drizzle', 45.00, 'BEVERAGES', TRUE, 5),
(5, 'Masala Chai', 'Hot spiced milk tea', 20.00, 'BEVERAGES', TRUE, 3);
