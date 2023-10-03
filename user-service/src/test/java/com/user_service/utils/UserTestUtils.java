package com.user_service.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.user_service.formater.TimeFormatter;
import com.user_service.payload.response.CommonResponseDTO;
import com.user_service.payload.response.UserResponseDTO;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserTestUtils {
    @Autowired
    private MessageSrc messageSrc;

    @Autowired
    private TimeFormatter timeFormatter;

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    public String generateUsername() {
        return RandomStringUtils.randomAlphanumeric(8, 20);
    }

    public String generatePassword() {
        return RandomStringUtils.randomAlphabetic(2, 3).toUpperCase()
                + RandomStringUtils.randomAlphabetic(2, 10).toLowerCase()
                + RandomStringUtils.random(2, "~!@#$%^&*".toCharArray())
                + RandomStringUtils.randomNumeric(2, 5);
    }



    public ResponseEntity<String> executeUserAPI(
            int port, String username, HttpMethod httpMethod, HttpHeaders headers, Object body
    ) {
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        TestRestTemplate restTemplate = new TestRestTemplate();
        return restTemplate.exchange(ApiUtils.userURL(port, username), httpMethod, entity, String.class);
    }


    public String mockBodyWhenGetInformationSuccess(String username, LocalDateTime timeExecute) throws JsonProcessingException {
        UserResponseDTO data = new UserResponseDTO(
                username,
                null,
                timeFormatter.format(timeExecute),
                timeFormatter.format(timeExecute)
        );
        CommonResponseDTO mockBody = new CommonResponseDTO(true, null, data);
        return objectMapper.writeValueAsString(mockBody);
    }
}
