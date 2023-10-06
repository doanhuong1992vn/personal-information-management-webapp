package com.user_service.controller.user_controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.user_service.UserServiceApplication;
import com.user_service.utils.AuthTestUtils;
import com.user_service.utils.UserTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.LinkedHashMap;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GetInformationApiTest {
    @LocalServerPort
    int port;

    @Autowired
    private AuthTestUtils authTestUtils;

    @Autowired
    private UserTestUtils userTestUtils;


    @Test
    void testGetInformation_whenSuccess() throws JsonProcessingException {
        String username = userTestUtils.generateUsername();
        String password = userTestUtils.generatePassword();
        String token = authTestUtils.getTokenAfterLogin(port, username, password);
        ResponseEntity<String> response = userTestUtils.executeUserAPI(port, username, HttpMethod.GET, token, null);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        LinkedHashMap<String, String> data = authTestUtils.getResponseData(response);
        Assertions.assertNotNull(data);
        Assertions.assertEquals(username, data.get("username"));
        Assertions.assertNotNull(data.get("lastLogin"));
        Assertions.assertNotNull(data.get("createTime"));
    }


    @Test
    void testGetInformation_whenFail_byUsingUsernameOfOtherInURI() throws JsonProcessingException {
        String usernameOfOther = userTestUtils.generateUsername();
        String passwordOfOther = userTestUtils.generatePassword();
        authTestUtils.executeRegister(port, usernameOfOther, passwordOfOther);
        String username = userTestUtils.generateUsername();
        String password = userTestUtils.generatePassword();
        String token = authTestUtils.getTokenAfterLogin(port, username, password);
        ResponseEntity<String> response = userTestUtils.executeUserAPI(port, usernameOfOther, HttpMethod.GET, token, null);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }


}
