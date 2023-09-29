package com.user_service.exception;

public record ErrorDetails(boolean success, String message, String detail) {
    public ErrorDetails(String message, String detail) {
        this(false, message, detail);
    }
}
