package com.user_service.payload.response;

import java.time.LocalDateTime;

public record LoginResponseDTO (String username, String token, LocalDateTime lastLogin) {
}
