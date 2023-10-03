package com.user_service.dto;

public record ErrorDetailsDTO (String field, Object rejectedValue, String codeMessage) {
}
