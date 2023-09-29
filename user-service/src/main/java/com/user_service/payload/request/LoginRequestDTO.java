package com.user_service.payload.request;


import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO (
        @NotBlank String username,
        @NotBlank String password) {
}
