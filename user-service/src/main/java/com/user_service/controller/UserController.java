package com.user_service.controller;

import com.user_service.exception.CommonError;
import com.user_service.exception.CustomValidationException;
import com.user_service.payload.request.PasswordRequestDTO;
import com.user_service.payload.request.UserRequestDTO;
import com.user_service.payload.response.CommonResponseDTO;
import com.user_service.payload.response.UserResponseDTO;
import com.user_service.service.UserService;
import com.user_service.utils.MessageSrc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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


    @Operation(
            summary = "Get user profile by username and token",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Query successful. Please see the data field for results!",
                            content = @Content(
                                    schema = @Schema(implementation = UserResponseDTO.class)
                            )),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Full authentication is required to access this resource!",
                            content = @Content(
                                    schema = @Schema(implementation = CommonError.class)
                            ))
            }
    )
    @GetMapping
    public ResponseEntity<CommonResponseDTO> getInformation(@PathVariable @NotBlank String username) throws AuthenticationException {
        UserResponseDTO userResponseDTO = userService.getInformation(username);
        CommonResponseDTO body = new CommonResponseDTO(true, null, userResponseDTO);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }


    @Operation(
            summary = "Update user profile by both username and token and request body includes birthday.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = """
                            - Birthday:
                                + New birthday cannot be blank!
                                + Format: yyyy-MM-dd
                                + Date of birth must not be in the past!
                                + User must not exceed 100 years of age!
                            """,
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UserRequestDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Updated user information successfully!",
                            content = @Content(
                                    schema = @Schema(implementation = CommonResponseDTO.class)
                            )),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Update user information failed. Please check field Error and try again!",
                            content = @Content(
                                    schema = @Schema(implementation = CommonError.class)
                            )),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Full authentication is required to access this resource!",
                            content = @Content(
                                    schema = @Schema(implementation = CommonError.class)
                            ))
            }
    )
    @PutMapping
    public ResponseEntity<CommonResponseDTO> updateProfile(
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
        UserResponseDTO userResponseDTO = userService.updateProfile(username, requestDTO);
        CommonResponseDTO body = new CommonResponseDTO(
                true, messageSrc.getMessage("Success.user.update"), userResponseDTO
        );
        return new ResponseEntity<>(body, HttpStatus.OK);
    }


    @Operation(
            summary = "Update password by both username and token and request body includes the current password and the new password",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = """
                            - Both current password and new password are required:
                                + Cannot be blank!
                                + The required length of the password must be between 8 and 20 characters!
                                + Must contain at least a special character in ~!@#$%^&* string!
                                + Must contain at least a number!
                                + Must contain at least an uppercase letters!
                                + Must contain at least a lowercase letters!
                            """,
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = PasswordRequestDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Updated new password successfully!",
                            content = @Content(
                                    schema = @Schema(implementation = CommonResponseDTO.class)
                            )),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Update user information failed. Please check field Error and try again!",
                            content = @Content(
                                    schema = @Schema(implementation = CommonError.class)
                            )),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Full authentication is required to access this resource!",
                            content = @Content(
                                    schema = @Schema(implementation = CommonError.class)
                            ))
            }
    )
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
                new CommonResponseDTO(true, messageSrc.getMessage("Success.user.password.update"), null),
                HttpStatus.OK
        )
                : new ResponseEntity<>(
                new CommonResponseDTO(false, messageSrc.getMessage("Error.user.password.update"), null),
                HttpStatus.BAD_REQUEST
        );
    }

}
