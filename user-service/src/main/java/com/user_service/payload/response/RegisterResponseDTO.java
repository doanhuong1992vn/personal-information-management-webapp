package com.user_service.payload.response;

import java.time.LocalDate;


public record RegisterResponseDTO (
        String username,
        String birthday
) {
}
