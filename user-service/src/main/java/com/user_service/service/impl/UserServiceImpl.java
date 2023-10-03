package com.user_service.service.impl;

import com.user_service.converter.UserConverter;
import com.user_service.entity.Role;
import com.user_service.entity.User;
import com.user_service.exception.CustomValidationException;
import com.user_service.exception.DuplicateUsernameException;
import com.user_service.payload.request.LoginRequestDTO;
import com.user_service.payload.request.PasswordRequestDTO;
import com.user_service.payload.request.RegisterRequestDTO;
import com.user_service.payload.request.UserRequestDTO;
import com.user_service.payload.response.LoginResponseDTO;
import com.user_service.payload.response.RegisterResponseDTO;
import com.user_service.payload.response.UserResponseDTO;
import com.user_service.repository.RoleRepository;
import com.user_service.repository.UserRepository;
import com.user_service.security.JwtTokenProvider;
import com.user_service.security.UserContextHolder;
import com.user_service.service.UserService;
import com.user_service.utils.MessageSrc;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider tokenProvider;

    private final MessageSrc messageSrc;

    private final UserConverter userConverter;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final UserContextHolder userContextHolder = UserContextHolder.getInstance();


    @Override
    public LoginResponseDTO login(LoginRequestDTO requestDTO) {
        try {
            String username = requestDTO.username();
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, requestDTO.password()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = userRepository.findById(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User "+username+"was not found in database!"));

            LocalDateTime lastLogin = user.getLastLogin();
            String token = tokenProvider.generateToken(authentication);
            userContextHolder.saveToken(username, token);
            Thread saveCurrentLogin = new Thread(() -> {
                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);
            });
            saveCurrentLogin.start();
            return new LoginResponseDTO(username, token, lastLogin);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(messageSrc.getMessage("Error.user.login.field"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public RegisterResponseDTO register(RegisterRequestDTO requestDTO) throws DuplicateUsernameException, CustomValidationException {
        String username = requestDTO.username();
        if (userRepository.existsById(username)) {
            throw new DuplicateUsernameException(messageSrc.getMessage("Error.user.username.exists", username));
        } else {
            User user = userConverter.convertRegisterRequestToEntity(requestDTO);
            validateBirthday(user.getBirthday());
            user.setPassword(hashPassword(requestDTO.password()));
            setRoleUser(user);
            user.setCreateTime(LocalDateTime.now());
            User newUser = userRepository.save(user);
            return userConverter.convertEntityToRegisterResponse(newUser);
        }
    }


    private void setRoleUser(User user) {
        Role roleUser = roleRepository.findByName(Role.RoleName.ROLE_USER);
        Set<Role> roles = Set.of(roleUser);
        user.setRoles(roles);
    }


    private void validateBirthday(LocalDate birthday) throws CustomValidationException {
        if (birthday == null) {
            return;
        }
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(birthday, currentDate);
        int age = period.getYears();
        if (age > 100) {
            List<FieldError> errorDetails = List.of(
                    new FieldError("birthday", "birthday", birthday,
                            false, new String[]{"TooOld"}, null,
                            messageSrc.getMessage("TooOld.user.birthday"))
            );
            throw new CustomValidationException(
                    messageSrc.getMessage("Error.user.register.validation"), errorDetails
            );
        }
    }


    @Override
    public boolean logout(String bearerToken) {
        String username = tokenProvider.extractUsername(bearerToken);
        String token = tokenProvider.getJwtFromBearerToken(bearerToken);
        return userContextHolder.removeBearerToken(username, token);
    }


    @Override
    public UserResponseDTO getInformation(String username) throws AuthenticationException {
        User user = checkCurrentUser(username);
        return userConverter.convertEntityToResponseDTO(user);
    }


    @Override
    public UserResponseDTO update(String username, UserRequestDTO requestDTO)
            throws AuthenticationException, CustomValidationException {
        User user = checkCurrentUser(username);
        validateBirthday(requestDTO.birthday());
        user.setBirthday(requestDTO.birthday());
        User result = userRepository.save(user);
        return userConverter.convertEntityToResponseDTO(result);
    }

    @Override
    public boolean update(String username, PasswordRequestDTO requestDTO) throws AuthenticationException {
        User user = checkCurrentUser(username);
        if (passwordEncoder.matches(requestDTO.oldPassword(), user.getPassword())) {
            user.setPassword(hashPassword(requestDTO.newPassword()));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }


    public User checkCurrentUser(String username) throws AuthenticationException {
        User user = getCurrentUser();
        if (user.getUsername().equals(username)) {
            return user;
        } else {
            throw new AuthenticationException("You are not permitted to access the information of others!");
        }
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails details = (UserDetails) authentication.getPrincipal();
        String username = details.getUsername();
        return userRepository.findById(username).orElseThrow();
    }

}
