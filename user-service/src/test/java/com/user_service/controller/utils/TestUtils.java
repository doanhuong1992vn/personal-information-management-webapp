package com.user_service.controller.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.user_service.exception.CommonError;
import com.user_service.exception.ErrorDetails;
import com.user_service.payload.request.LoginRequestDTO;
import com.user_service.payload.request.RegisterRequestDTO;
import com.user_service.utils.MessageUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Component
public class TestUtils {
    @Autowired
    private MessageUtils messageUtils;

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    static final String host = "http://localhost:";

    static final String registerAPI = "/api/auth/register";

    static final String loginAPI = "/api/auth/login";

    static final String logoutAPI = "/api/auth/logout";


    public String getRegisterURL(int port) {
        return host + port + registerAPI;
    }


    public String getLoginURL(int port) {
        return host + port + loginAPI;
    }


    public String getLogoutURL(int port) {
        return host + port + logoutAPI;
    }


    public ResponseEntity<String> executeRegister(int port, String username, String password) {
        return executeRegister(port, username, password, null);
    }


    public ResponseEntity<String> executeRegister(int port, String username, String password, LocalDate birthday) {
        TestRestTemplate restTemplate = new TestRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        RegisterRequestDTO body = new RegisterRequestDTO(username, password, birthday);
        HttpEntity<RegisterRequestDTO> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(
                getRegisterURL(port), HttpMethod.POST, entity, String.class
        );
    }


    public HttpResponse executeLoginByHttpClient(int port, String username, String password) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(getLoginURL(port));
        httpPost.setHeader("Content-Type", "application/json");
        LoginRequestDTO body = new LoginRequestDTO(username, password);
        String entity = new ObjectMapper().writeValueAsString(body);
        StringEntity requestBody = new StringEntity(entity);
        httpPost.setEntity(requestBody);
        return httpClient.execute(httpPost);
    }


    public String mockConstraintValidationBody(List<ErrorDetailsDTO> errorDetailsDTOList, String codeMessage) throws JsonProcessingException {
        List<ErrorDetails> mockErrors = errorDetailsDTOList
                .parallelStream()
                .map(error -> new ErrorDetails(error.field(), error.rejectedValue(), messageUtils.getMessage(error.codeMessage())))
                .toList();
        CommonError mockBody = new CommonError(
                messageUtils.getMessage(codeMessage), mockErrors
        );
        return objectMapper.writeValueAsString(mockBody);
    }


}
