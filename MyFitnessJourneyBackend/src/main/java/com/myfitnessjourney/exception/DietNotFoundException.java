package com.myfitnessjourney.exception;

public class DietNotFoundException extends RuntimeException {
    public DietNotFoundException(String message) {
        super(message);
    }
}

