package com.user_service.controller;

import com.user_service.exception.CustomValidationException;
import com.user_service.payload.request.PasswordRequestDTO;
import com.user_service.payload.request.UserRequestDTO;
import com.user_service.payload.response.CommonResponseDTO;
import com.user_service.payload.response.UserResponseDTO;
import com.user_service.service.UserService;
import com.user_service.utils.MessageSrc;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/{username}")
@RequiredArgsConstructor
@Tag(
        name = "User Management",
        description = "API endpoints for user management"
)
public class UserController {
    private final UserService userService;

    private final MessageSrc messageSrc;

    @GetMapping
    public ResponseEntity<CommonResponseDTO> getInformation(@PathVariable @NotBlank String username) throws AuthenticationException {
        UserResponseDTO userResponseDTO = userService.getInformation(username);
        CommonResponseDTO body = new CommonResponseDTO(true, null, userResponseDTO);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }


    @PutMapping
    public ResponseEntity<CommonResponseDTO> update(
            @PathVariable @NotBlank String username,
            @RequestBody @Valid UserRequestDTO requestDTO,
            Errors errors
    ) throws AuthenticationException, CustomValidationException {
        if (errors.hasErrors()) {
            throw new CustomValidationException(
                    messageSrc.getMessage("Error.user.update"),
                    errors.getFieldErrors()
            );
        }
        UserResponseDTO userResponseDTO = userService.updatePassword(username, requestDTO);
        CommonResponseDTO body = new CommonResponseDTO(
                true, messageSrc.getMessage("Success.user.update"), userResponseDTO
        );
        return new ResponseEntity<>(body, HttpStatus.OK);
    }


    @PatchMapping
    public ResponseEntity<CommonResponseDTO> updatePassword(
            @PathVariable @NotBlank String username,
            @RequestBody @Valid PasswordRequestDTO requestDTO,
            Errors errors
    ) throws CustomValidationException, AuthenticationException {
        if (errors.hasErrors()) {
            throw new CustomValidationException(
                    messageSrc.getMessage("Error.user.update"),
                    errors.getFieldErrors()
            );
        }
        return userService.updatePassword(username, requestDTO)
                ? new ResponseEntity<>(
                new CommonResponseDTO(true, messageSrc.getMessage("Success.user.update"), null),
                HttpStatus.OK
        )
                : new ResponseEntity<>(
                new CommonResponseDTO(false, messageSrc.getMessage("Error.user.password.update"), null),
                HttpStatus.BAD_REQUEST
        );


    }

}
