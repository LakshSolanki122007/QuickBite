package com.quickbite.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // Hashes a plain-text password using BCrypt with workload factor 12
    public static String hashPassword(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(12));
    }

    // Verifies a plain-text password against a stored BCrypt hash
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || hashedPassword == null || !hashedPassword.startsWith("$2a$")) {
            return false;
        }
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}