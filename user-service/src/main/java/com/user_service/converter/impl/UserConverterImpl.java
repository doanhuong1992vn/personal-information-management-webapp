package com.user_service.converter.impl;

import com.user_service.entity.User;
import com.user_service.payload.request.RegisterRequestDTO;
import com.user_service.converter.UserConverter;
import com.user_service.payload.response.RegisterResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class UserConverterImpl implements UserConverter {

    @Override
    public User convertRegisterRequestToEntity(RegisterRequestDTO requestDTO) {
        return User.builder()
                .username(requestDTO.username())
                .password(requestDTO.password())
                .birthday(requestDTO.birthday())
                .build();
    }

    @Override
    public RegisterResponseDTO convertEntityToRegisterResponse(User user) {
        return new RegisterResponseDTO(user.getUsername(), user.getBirthday());
    }
}
