package com.user_service.controller.auth_controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.user_service.UserServiceApplication;
import com.user_service.utils.AuthTestUtils;
import com.user_service.utils.UserTestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LogoutApiTest {
    @LocalServerPort
    int port;

    @Autowired
    private AuthTestUtils authTestUtils;

    @Autowired
    private UserTestUtils userTestUtils;


    @Test
    void testLogout_whenSuccess() throws JsonProcessingException, JSONException {
        String username = userTestUtils.generateUsername();
        String password = userTestUtils.generatePassword();
        String token = authTestUtils.getTokenAfterLogin(port, username, password);
        ResponseEntity<String> response =  authTestUtils.executeLogout(port, token);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        String expected = authTestUtils.mockBodyWhenLogoutSuccess();
        JSONAssert.assertEquals(expected, response.getBody(), true);
    }


    @Test
    void testLogout_whenFail_byInvalidToken() {
        String invalidToken = RandomStringUtils.randomAlphabetic(128, 356);
        ResponseEntity<String> response = authTestUtils.executeLogout(port, invalidToken);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Assertions.assertNull(response.getBody());
    }


    @Test
    void testLogout_whenFail_byExpiredToken() {
        String username = userTestUtils.generateUsername();
        String password = userTestUtils.generatePassword();
        authTestUtils.executeRegister(port, username, password);
        String expiredToken = authTestUtils.generateExpiredTokenByUsername(username);
        ResponseEntity<String> response =  authTestUtils.executeLogout(port, expiredToken);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Assertions.assertNull(response.getBody());
    }


}
