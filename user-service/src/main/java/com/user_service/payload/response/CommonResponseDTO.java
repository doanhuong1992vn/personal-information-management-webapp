package com.user_service.payload.response;

public record CommonResponseDTO (boolean success, String message, Object data) {
}
