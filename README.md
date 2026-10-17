# QuickBite | Canteen Management System 🍔🚀

QuickBite is a lightning-fast, modern Canteen Management System designed to eliminate long queues and streamline food ordering for students and staff. It features a sleek, dark-mode user interface, real-time order token tracking, interactive status indicators, browser push notifications, and a robust backend architecture.

---

## ✨ Key Features

* **Interactive Canteen Status Switch**: Toggle the canteen state between **Open** and **Closed** instantly, which automatically enables or disables ordering on the frontend[cite: 1].
* **Category-Based Menu Filtering**: Seamlessly filter items across Burgers, Pizzas, Sides & Fries, Beverages, and Desserts with real-time search capabilities[cite: 1].
* **Real-Time Cart & Slot Pre-booking**: Manage quantities dynamically, calculate subtotals with automated taxes (5%), and select express pickup time slots[cite: 1].
* **Order Tracking & Digital Tokens**: Generate unique tokens (e.g., `#01`, `#02`) upon checkout with automated order progression from *Preparing* to *Ready to Pick Up*[cite: 1].
* **Native Browser Notifications**: Sends instant device-level push alerts when an order is ready for collection[cite: 4].
* **In-App Pickup Confirmation**: Inline confirmation flow allowing users to verify collection without disrupting the dark-mode immersive UI[cite: 5].

---

## 🛠️ Tech Stack

* **Frontend**: HTML5, Tailwind CSS (via CDN), JavaScript (ES6+), Lucide Icons, HTML5 Notifications API[cite: 1].
* **Backend**: Java, Javalin framework[cite: 1].
* **Database & Storage**: MySQL / Persistent Data Handling via DAO layers[cite: 1].
* **Development & Deployment**: Visual Studio Code, Port Forwarding / Tunnels[cite: 1].

---

## 📂 Project Structure

```text
QuickBite/
│
├── src/main/java/com/quickbite/
│   ├── controller/      # Order and menu route controllers
│   ├── dao/             # Data Access Objects for database interactions
│   ├── exception/       # Custom application exceptions
│   ├── model/           # Entity models (User, Order, MenuItem, etc.)
│   ├── service/         # Business logic layer
│   └── Main.java        # Application entry point and server startup
│
├── index.html           # Responsive Tailwind CSS frontend interface
├── Database.sql         # Database schema and initial seed data
└── pom.xml              # Maven configuration file
