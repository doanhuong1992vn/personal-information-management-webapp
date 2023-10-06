package com.user_service.exception;

public record CommonError(boolean success, String message, Object error) {
    public CommonError(String message, Object error) {
        this(false, message, error);
    }
}
