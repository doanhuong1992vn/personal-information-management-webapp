package com.user_service.converter;

import com.user_service.entity.User;
import com.user_service.payload.request.RegisterRequestDTO;
import com.user_service.payload.response.RegisterResponseDTO;

public interface UserConverter {
    User convertRegisterRequestToEntity(RegisterRequestDTO requestDTO);

    RegisterResponseDTO convertEntityToRegisterResponse(User user);
}
