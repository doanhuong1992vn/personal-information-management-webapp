package com.user_service.security;

import com.user_service.exception.InvalidTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    private final UserContextHolder userContextHolder = UserContextHolder.getInstance();

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String bearerToken = request.getHeader("Authorization");
            if (bearerToken != null && tokenProvider.isValidToken(bearerToken)) {
                String username = tokenProvider.extractUsername(bearerToken);
                String token = tokenProvider.getJwtFromBearerToken(bearerToken);
                if (userContextHolder.isValidLoggedUser(username, token)) {
                    UserDetails userDetails = userContextHolder.getUserDetails(username);
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context");
            logger.error(ex.getMessage());
            throw new InvalidTokenException(ex.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
