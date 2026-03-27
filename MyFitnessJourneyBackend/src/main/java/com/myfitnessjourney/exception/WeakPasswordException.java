package com.myfitnessjourney.exception;

public class WeakPasswordException extends IllegalArgumentException {
    public WeakPasswordException(String message) {
        super(message);
    }
}

