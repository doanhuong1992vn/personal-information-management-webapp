package com.user_service.payload.request;

import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record UserRequestDTO(
        @Past(message = "{Past.user.birthday}")
        LocalDate birthday)
{
}
