package com.user_service.controller;

import com.user_service.payload.response.CheckerResponseDTO;
import com.user_service.payload.response.CommonResponseDTO;
import com.user_service.service.UserService;
import com.user_service.utils.MessageSrc;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
@Tag(
        name = "Public Resources",
        description = "API endpoints for public resources"
)
public class PublicController {
    private final UserService userService;

    private final MessageSrc messageSrc;

    @GetMapping("/check-username/{username}")
    public ResponseEntity<CommonResponseDTO> checkUsername(@PathVariable String username) {
        return userService.existsByUsername(username)
                ? new ResponseEntity<>(
                new CommonResponseDTO(
                        true,
                        messageSrc.getMessage("Success.check"),
                        new CheckerResponseDTO(true, messageSrc.getMessage("Exists.username.true", username))
                ),
                HttpStatus.OK
        )
                : new ResponseEntity<>(
                new CommonResponseDTO(
                        true,
                        messageSrc.getMessage("Success.check"),
                        new CheckerResponseDTO(false, messageSrc.getMessage("Exists.username.false", username))
                ),
                HttpStatus.OK
        );
    }
}
