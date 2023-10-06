package com.user_service.exception;

public record ErrorDetails (String field, Object rejectedValue, String message) {
}
