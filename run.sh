#!/bin/bash

# 1. Ensure MySQL service is running
brew services start mysql

# 2. Launch both tabs in Google Chrome
sleep 2
open -a "Google Chrome" "http://localhost:8080"
open -a "Google Chrome" "http://localhost:8080/admin.html"

# 3. Compile and start the Javalin server
mvn compile exec:java -Dexec.mainClass="com.quickbite.Main"