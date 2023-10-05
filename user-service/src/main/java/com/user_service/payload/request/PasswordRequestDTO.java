package com.user_service.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PasswordRequestDTO(
        @NotBlank(message = "{Blank.user.password}")
        @Size(min = 8, max = 20, message = "{Size.user.password}")
        @Pattern(regexp = "^(?=.*[~!@#$%^&*]).{2,}$", message = "{Pattern.user.password.special-characters}")
        @Pattern(regexp = "^(?=.*\\d).{2,}$", message = "{Pattern.user.password.number}")
        @Pattern(regexp = "^(?=.*[A-Z]).{2,}$", message = "{Pattern.user.password.uppercase-letters}")
        @Pattern(regexp = "^(?=.*[a-z]).{2,}$", message = "{Pattern.user.password.lowercase-letters}")
        String currentPassword,

        @NotBlank(message = "{Blank.user.password}")
        @Size(min = 8, max = 20, message = "{Size.user.password}")
        @Pattern(regexp = "^(?=.*[~!@#$%^&*]).{2,}$", message = "{Pattern.user.password.special-characters}")
        @Pattern(regexp = "^(?=.*\\d).{2,}$", message = "{Pattern.user.password.number}")
        @Pattern(regexp = "^(?=.*[A-Z]).{2,}$", message = "{Pattern.user.password.uppercase-letters}")
        @Pattern(regexp = "^(?=.*[a-z]).{2,}$", message = "{Pattern.user.password.lowercase-letters}")
        String newPassword
        ) {

}
