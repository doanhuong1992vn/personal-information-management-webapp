package com.user_service.controller;

import com.user_service.exception.CustomValidationException;
import com.user_service.exception.DuplicateUsernameException;
import com.user_service.payload.request.LoginRequestDTO;
import com.user_service.payload.request.RegisterRequestDTO;
import com.user_service.payload.response.CommonResponseDTO;
import com.user_service.payload.response.LoginResponseDTO;
import com.user_service.payload.response.RegisterResponseDTO;
import com.user_service.service.UserService;
import com.user_service.utils.MessageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication",
        description = "API endpoints for authentication"
)
//@Validated
public class AuthController {
    private final MessageUtils messageUtils;
    private final UserService userService;


    @Operation(
            summary = "Register new user",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User register information:",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RegisterRequestDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Register new user successfully!"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "User information error!"
                    )
            }
    )
    @PostMapping("/register")
    public ResponseEntity<CommonResponseDTO> register(
            @RequestBody @Valid RegisterRequestDTO requestDTO, Errors errors
    ) throws DuplicateUsernameException, CustomValidationException {
        if (errors.hasErrors()) {
            throw new CustomValidationException(
                    messageUtils.getMessage("Error.user.register.validation"),
                    errors.getFieldErrors()
            );
        }
        RegisterResponseDTO data = userService.register(requestDTO);
        return new ResponseEntity<>(
                new CommonResponseDTO(true, messageUtils.getMessage("Success.user.register"), data),
                HttpStatus.CREATED
        );
    }


    @Operation(
            summary = "Login using username and password",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Username and Password:",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LoginRequestDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login successfully!",
                            content = @Content(
                                    schema = @Schema(implementation = LoginResponseDTO.class)
                            )),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthenticated!"
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<CommonResponseDTO> login(@RequestBody @Valid LoginRequestDTO requestDTO, Errors errors) throws CustomValidationException {
        if (errors.hasErrors()) {
            throw new CustomValidationException(
                    messageUtils.getMessage("Error.user.login"),
                    errors.getFieldErrors()
            );
        }
        LoginResponseDTO data = userService.login(requestDTO);
        return data == null
                ? new ResponseEntity<>(
                new CommonResponseDTO(false, messageUtils.getMessage("Error.user.login"), null),
                HttpStatus.UNAUTHORIZED
        )
                : new ResponseEntity<>(
                new CommonResponseDTO(true, messageUtils.getMessage("Success.user.login"), data),
                HttpStatus.OK
        );
    }


    @Operation(
            summary = "Logout",
            description = "Logout the user by invalidating the provided bearer token.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Logout successful.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommonResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid token.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommonResponseDTO.class)
                            )
                    )
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<CommonResponseDTO> logout (@RequestHeader(name = "Authorization") String bearerToken) {
        if (userService.logout(bearerToken)) {
            return new ResponseEntity<>(
                    new CommonResponseDTO(true, messageUtils.getMessage("Success.user.logout"), null),
                    HttpStatus.OK
            );
        } else {
            return new ResponseEntity<>(
                    new CommonResponseDTO(false, messageUtils.getMessage("Error.user.logout"), null),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
