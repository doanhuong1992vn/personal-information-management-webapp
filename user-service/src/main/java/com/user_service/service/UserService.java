package com.user_service.service;

import com.user_service.exception.CustomValidationException;
import com.user_service.exception.DuplicateUsernameException;
import com.user_service.payload.request.LoginRequestDTO;
import com.user_service.payload.request.RegisterRequestDTO;
import com.user_service.payload.response.LoginResponseDTO;
import com.user_service.payload.response.RegisterResponseDTO;

public interface UserService {
    LoginResponseDTO login(LoginRequestDTO userLoginRequestDto);

    RegisterResponseDTO register(RegisterRequestDTO requestDTO) throws DuplicateUsernameException, CustomValidationException;

    boolean logout(String bearerToken);
}
