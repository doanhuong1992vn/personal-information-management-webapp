package com.user_service.controller.utils;

public record ErrorDetailsDTO (String field, Object rejectedValue, String codeMessage) {
}
