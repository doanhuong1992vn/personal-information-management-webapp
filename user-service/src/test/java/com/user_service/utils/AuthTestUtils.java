package com.user_service.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.user_service.dto.ErrorDetailsDTO;
import com.user_service.exception.CommonError;
import com.user_service.exception.ErrorDetails;
import com.user_service.payload.request.LoginRequestDTO;
import com.user_service.payload.request.RegisterRequestDTO;
import com.user_service.payload.response.CommonResponseDTO;
import com.user_service.payload.response.LoginResponseDTO;
import com.user_service.security.JwtTokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Component
public class AuthTestUtils {
    @Autowired
    private MessageSrc messageSrc;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Value("${app.jwtExpirationInMs}")
    private Long jwtExpirationInMs;

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    public ResponseEntity<String> executeRegister(int port, String username, String password) {
        return executeRegister(port, username, password, null);
    }


    public ResponseEntity<String> executeRegister(int port, String username, String password, LocalDate birthday) {
        TestRestTemplate restTemplate = new TestRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        RegisterRequestDTO body = new RegisterRequestDTO(username, password, birthday);
        HttpEntity<RegisterRequestDTO> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(ApiUtils.registerURL(port), HttpMethod.POST, entity, String.class);
    }


    public HttpResponse executeLoginByHttpClient(int port, String username, String password) throws IOException, URISyntaxException {
        LoginRequestDTO body = new LoginRequestDTO(username, password);
        return ApiUtils.execute(new HttpPost(), new URI(ApiUtils.loginURL(port)), null, body);
    }


    public ResponseEntity<String> executeLoginByTestRestTemplate(int port, String username, String password) {
        TestRestTemplate restTemplate = new TestRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        LoginRequestDTO body = new LoginRequestDTO(username, password);
        HttpEntity<LoginRequestDTO> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(ApiUtils.loginURL(port), HttpMethod.POST, entity, String.class);
    }


    public ResponseEntity<String> executeLogout(int port, HttpHeaders headers) {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        TestRestTemplate restTemplate = new TestRestTemplate();
        return restTemplate.exchange(ApiUtils.logoutURL(port), HttpMethod.POST, entity, String.class);
    }


    public HttpHeaders getAuthorizationHeader(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);
        return headers;
    }


    public String mockBodyWhenLoginSuccess(String username, String token) throws JsonProcessingException {
        LoginResponseDTO mockData = new LoginResponseDTO(username, token, null);
        CommonResponseDTO mockBody = new CommonResponseDTO(
                true, messageSrc.getMessage("Success.user.login"), mockData
        );
        return objectMapper.writeValueAsString(mockBody);
    }


    public String mockDefaultBodyWhenLoginFail() throws JsonProcessingException {
        CommonError mockBody = new CommonError(
                messageSrc.getMessage("Error.user.login"),
                messageSrc.getMessage("Error.user.login.field")
        );
        return objectMapper.writeValueAsString(mockBody);
    }


    public String mockBodyWhenLogoutSuccess() throws JsonProcessingException {
        CommonResponseDTO mockBody = new CommonResponseDTO(
                true, messageSrc.getMessage("Success.user.logout"), null
        );
        return objectMapper.writeValueAsString(mockBody);
    }


    public String mockBodyWhenValidationException(List<ErrorDetailsDTO> errorDetailsDTOList, String codeMessage) throws JsonProcessingException {
        List<ErrorDetails> mockErrors = errorDetailsDTOList
                .parallelStream()
                .map(error -> new ErrorDetails(error.field(), error.rejectedValue(), messageSrc.getMessage(error.codeMessage())))
                .toList();
        CommonError mockBody = new CommonError(
                messageSrc.getMessage(codeMessage), mockErrors
        );
        return objectMapper.writeValueAsString(mockBody);
    }


    public String getTokenAndValidate(ResponseEntity<String> response) throws JsonProcessingException {
        LinkedHashMap<String, String> responseData = getResponseData(response);
        String token = responseData.get("token");
        Assertions.assertNotNull(token);
        Assertions.assertFalse(token.isEmpty());
        return token;
    }

    public LinkedHashMap<String, String> getResponseData(ResponseEntity<String> response) throws JsonProcessingException {
        TypeReference<CommonResponseDTO> typeReference = new TypeReference<>() {
        };
        CommonResponseDTO responseBody = objectMapper.readValue(response.getBody(), typeReference);
        Assertions.assertNotNull(responseBody);
        return (LinkedHashMap) responseBody.data();
    }


    public String generateExpiredTokenByUsername(String username) {
        LocalDateTime aYearAgo = LocalDateTime.now().minusYears(1);
        Date issueAt = Date.from(aYearAgo.toInstant(ZoneOffset.UTC));
        Date expiryDate = new Date(issueAt.getTime() + jwtExpirationInMs);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(issueAt)
                .setExpiration(expiryDate)
                .signWith(tokenProvider.getSecretKey(), SignatureAlgorithm.HS512)
                .compact();
    }


    public String getTokenAfterLogin(int port, String username, String password) throws JsonProcessingException {
        executeRegister(port, username, password);
        ResponseEntity<String> responseLogin = executeLoginByTestRestTemplate(port, username, password);
        return getTokenAndValidate(responseLogin);
    }


}
