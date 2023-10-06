package com.user_service.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegisterRequestDTO(
        @NotBlank(message = "{Blank.user.username}")
        @Size(min = 8, max = 20, message = "{Size.user.username}")
        @Pattern(regexp = "^[a-zA-Z0-9_]{2,}$", message = "{Pattern.user.username.regex}")
        String username,

        @NotBlank(message = "{Blank.user.password}")
        @Size(min = 8, max = 20, message = "{Size.user.password}")
        @Pattern(regexp = "^(?=.*[~!@#$%^&*]).{2,}$", message = "{Pattern.user.password.special-characters}")
        @Pattern(regexp = "^(?=.*\\d).{2,}$", message = "{Pattern.user.password.number}")
        @Pattern(regexp = "^(?=.*[A-Z]).{2,}$", message = "{Pattern.user.password.uppercase-letters}")
        @Pattern(regexp = "^(?=.*[a-z]).{2,}$", message = "{Pattern.user.password.lowercase-letters}")
        String password,

        @Past(message = "{Past.user.birthday}")
        LocalDate birthday
) {
}
