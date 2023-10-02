package com.user_service.controller.dto;

public record ErrorDetailsDTO (String field, Object rejectedValue, String codeMessage) {
}
