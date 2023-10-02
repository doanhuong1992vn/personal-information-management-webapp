package com.user_service.controller.auth_controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.user_service.UserServiceApplication;
import com.user_service.controller.utils.AuthTestUtils;
import com.user_service.utils.MessageUtils;
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
public class LogoutMethodTest {
    @LocalServerPort
    int port;

    @Autowired
    private AuthTestUtils authTestUtils;


    @Test
    void testLogout_whenSuccess() throws JsonProcessingException, JSONException {
        String token = authTestUtils.getTokenAfterLogin(port);
        HttpHeaders headers = authTestUtils.getAuthorizationHeader(token);
        ResponseEntity<String> response =  authTestUtils.executeLogout(port, headers);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        String expected = authTestUtils.mockBodyWhenLogoutSuccess();
        JSONAssert.assertEquals(expected, response.getBody(), true);
    }


    @Test
    void testLogout_whenFail_byInvalidToken() {
        String invalidToken = RandomStringUtils.randomAlphabetic(128, 356);
        HttpHeaders headers = authTestUtils.getAuthorizationHeader(invalidToken);
        ResponseEntity<String> response = authTestUtils.executeLogout(port, headers);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Assertions.assertNull(response.getBody());
    }


    @Test
    void testLogout_whenFail_byExpiredToken() {
        String username = "expiredToken";
        String password = "Glsoft@123";
        authTestUtils.executeRegister(port, username, password);
        String expiredToken = authTestUtils.generateExpiredTokenByUsername(username);
        HttpHeaders headers = authTestUtils.getAuthorizationHeader(expiredToken);
        ResponseEntity<String> response =  authTestUtils.executeLogout(port, headers);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Assertions.assertNull(response.getBody());
    }


}
