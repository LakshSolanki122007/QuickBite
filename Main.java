package com.quickbite;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class Main {
    public static void main(String[] args) {
        
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public", Location.CLASSPATH);
        }).start(7000);

        
        app.get("/", ctx -> {
            ctx.result("QuickBite Canteen Management System Server is Running!");
        });

        
        app.get("/api/health", ctx -> ctx.json("{\"status\": \"UP\"}"));
        
        System.out.println("QuickBite Server successfully started on http://localhost:7000");
    }
}