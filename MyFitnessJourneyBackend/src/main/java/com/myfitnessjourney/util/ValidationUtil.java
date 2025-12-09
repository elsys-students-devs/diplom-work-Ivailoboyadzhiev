package com.myfitnessjourney.util;

import com.myfitnessjourney.exception.InvalidEmailException;
import com.myfitnessjourney.exception.WeakPasswordException;

public class ValidationUtil {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidEmailException("Email is required");
        }
        if (!email.contains("@")) {
            throw new InvalidEmailException("Email must contain @ symbol");
        }
        if (!email.matches(EMAIL_REGEX)) {
            throw new InvalidEmailException("Please enter a valid email address (e.g., user@example.com)");
        }
    }

    public static void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new WeakPasswordException("Password is required");
        }
        if (password.length() < 6) {
            throw new WeakPasswordException("Password must be at least 6 characters long");
        }
        if (password.length() > 50) {
            throw new WeakPasswordException("Password must be less than 50 characters");
        }
        // Check for at least one letter and one number
        boolean hasLetter = password.matches(".*[A-Za-z].*");
        boolean hasNumber = password.matches(".*[0-9].*");
        if (!hasLetter || !hasNumber) {
            throw new WeakPasswordException("Password must contain at least one letter and one number");
        }
    }
}

