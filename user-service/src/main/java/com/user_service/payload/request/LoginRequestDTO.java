package com.user_service.payload.request;


import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO (
        @NotBlank(message = "{Blank.user.username}")
        String username,

        @NotBlank(message = "{Blank.user.password}")
        String password) {
}
