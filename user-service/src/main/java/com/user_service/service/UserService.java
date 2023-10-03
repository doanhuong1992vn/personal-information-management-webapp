package com.user_service.service;

import com.user_service.exception.CustomValidationException;
import com.user_service.exception.DuplicateUsernameException;
import com.user_service.payload.request.LoginRequestDTO;
import com.user_service.payload.request.PasswordRequestDTO;
import com.user_service.payload.request.RegisterRequestDTO;
import com.user_service.payload.request.UserRequestDTO;
import com.user_service.payload.response.LoginResponseDTO;
import com.user_service.payload.response.RegisterResponseDTO;
import com.user_service.payload.response.UserResponseDTO;
import org.apache.tomcat.websocket.AuthenticationException;

public interface UserService {
    LoginResponseDTO login(LoginRequestDTO userLoginRequestDto);

    RegisterResponseDTO register(RegisterRequestDTO requestDTO) throws DuplicateUsernameException, CustomValidationException;

    boolean logout(String bearerToken);

    UserResponseDTO getInformation(String username) throws AuthenticationException;

    UserResponseDTO update(String username, UserRequestDTO requestDTO) throws AuthenticationException, CustomValidationException;

    boolean update(String username, PasswordRequestDTO requestDTO) throws AuthenticationException;
}
