package com.user_service.configuration;

import com.user_service.utils.MessageSrc;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {
    private final MessageSrc messageSrc;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        System.err.println(authException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, messageSrc.getMessage("Error.authentication"));
    }
}
