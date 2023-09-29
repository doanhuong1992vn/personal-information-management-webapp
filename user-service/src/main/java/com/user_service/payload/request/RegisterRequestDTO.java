package com.user_service.payload.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record RegisterRequestDTO (
        @NotBlank String username,
        @NotBlank String password,
        LocalDate birthday
) {
}
