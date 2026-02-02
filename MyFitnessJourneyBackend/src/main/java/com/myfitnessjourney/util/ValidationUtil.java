package com.myfitnessjourney.util;

import com.myfitnessjourney.exception.InvalidEmailException;
import com.myfitnessjourney.exception.InvalidUsernameException;
import com.myfitnessjourney.exception.WeakPasswordException;

public class ValidationUtil {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{3,20}$";

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
        // Check for at least one letter and one number (avoid ReDoS from regex on user input)
        boolean hasLetter = false;
        boolean hasNumber = false;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasNumber = true;
            if (hasLetter && hasNumber) break;
        }
        if (!hasLetter || !hasNumber) {
            throw new WeakPasswordException("Password must contain at least one letter and one number");
        }
    }

    public static void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidUsernameException("Username is required");
        }
        String trimmedUsername = username.trim();
        if (trimmedUsername.length() < 3) {
            throw new InvalidUsernameException("Username must be at least 3 characters long");
        }
        if (trimmedUsername.length() > 20) {
            throw new InvalidUsernameException("Username must be less than 20 characters");
        }
        if (!trimmedUsername.matches(USERNAME_REGEX)) {
            throw new InvalidUsernameException("Username can only contain letters, numbers, and underscores");
        }
    }
}

