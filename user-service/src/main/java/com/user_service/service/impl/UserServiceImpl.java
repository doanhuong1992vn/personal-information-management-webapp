package com.user_service.service.impl;

import com.user_service.security.JwtTokenHolder;
import com.user_service.security.JwtTokenProvider;
import com.user_service.converter.UserConverter;
import com.user_service.entity.Role;
import com.user_service.entity.User;
import com.user_service.exception.DuplicateUsernameException;
import com.user_service.payload.request.LoginRequestDTO;
import com.user_service.payload.request.RegisterRequestDTO;
import com.user_service.payload.response.LoginResponseDTO;
import com.user_service.payload.response.RegisterResponseDTO;
import com.user_service.repository.RoleRepository;
import com.user_service.repository.UserRepository;
import com.user_service.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider tokenProvider;

    private final UserConverter userConverter;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final JwtTokenHolder tokenHolder = JwtTokenHolder.getInstance();


    @Override
    public LoginResponseDTO login(LoginRequestDTO requestDTO) {
        try {
            String username = requestDTO.username();
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, requestDTO.password()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            Thread thread = new Thread(() -> {
                User user = userRepository.findById(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User "+username+"was not found in database!"));
                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);
            });
            thread.start();

            String token = tokenProvider.generateToken(authentication);
            tokenHolder.saveBearerToken(username, token);
            return new LoginResponseDTO(username, token);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public RegisterResponseDTO register(RegisterRequestDTO requestDTO) throws DuplicateUsernameException {
        if (userRepository.existsById(requestDTO.username())) {
            throw new DuplicateUsernameException(String.format(
                    "Username %s already exists!", requestDTO.username()));
        } else {
            User user = userConverter.convertRegisterRequestToEntity(requestDTO);
            String hashedPassword = BCrypt.hashpw(requestDTO.password(), BCrypt.gensalt(10));
            user.setPassword(hashedPassword);
            Role roleUser = roleRepository.findByName(Role.RoleName.ROLE_USER);
            Set<Role> roles = Set.of(roleUser);
            user.setRoles(roles);
            user.setCreateTime(LocalDateTime.now());
            User newUser = userRepository.save(user);
            return userConverter.convertEntityToRegisterResponse(newUser);
        }
    }

    @Override
    public boolean logout(String bearerToken) {
        String username = tokenProvider.extractUsername(bearerToken);
        String token = tokenProvider.getJwtFromBearerToken(bearerToken);
        return tokenHolder.removeBearerToken(username, token);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails details = (UserDetails) authentication.getPrincipal();
        String username = details.getUsername();
        return userRepository.findById(username).orElseThrow();
    }

}
