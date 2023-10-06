package com.user_service.controller.auth_controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.user_service.UserServiceApplication;
import com.user_service.dto.ErrorDetailsDTO;
import com.user_service.utils.AuthTestUtils;
import com.user_service.utils.UserTestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginApiTest {
    @LocalServerPort
    int port;

    @Autowired
    private AuthTestUtils authTestUtils;

    @Autowired
    private UserTestUtils userTestUtils;


    @Test
    void testLogin_whenSuccess() throws JsonProcessingException, JSONException {
        String username = userTestUtils.generateUsername();
        String password = userTestUtils.generatePassword();
        authTestUtils.executeRegister(port, username, password);
        ResponseEntity<String> response = authTestUtils.executeLoginByTestRestTemplate(port, username, password);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        String token = authTestUtils.getTokenAndValidate(response);
        String expected = authTestUtils.mockBodyWhenLoginSuccess(username, token);
        JSONAssert.assertEquals(expected, response.getBody(), true);
    }


    @Test
    void testLogin_whenFail_byWrongUsername() throws IOException, JSONException, URISyntaxException {
        String username = userTestUtils.generateUsername().substring(0, 8);
        String password = userTestUtils.generatePassword();
        authTestUtils.executeRegister(port, username, password);
        HttpResponse response = authTestUtils.executeLoginByHttpClient(port, username + "123", password);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        Assertions.assertNotNull(responseBody);
        String expected = authTestUtils.mockDefaultBodyWhenLoginFail();
        JSONAssert.assertEquals(expected, responseBody, true);
    }


    @Test
    void testLogin_whenFail_byWrongPassword() throws IOException, JSONException, URISyntaxException {
        String username = userTestUtils.generateUsername();
        String password = "Glsoft@123";
        authTestUtils.executeRegister(port, username, password);
        HttpResponse response = authTestUtils.executeLoginByHttpClient(port, username, password + "@123");
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        Assertions.assertNotNull(responseBody);
        String expected = authTestUtils.mockDefaultBodyWhenLoginFail();
        JSONAssert.assertEquals(expected, responseBody, true);
    }


    @Test
    void testLogin_whenFail_byWrongBothUsernameAndPassword() throws IOException, JSONException, URISyntaxException {
        String username = userTestUtils.generateUsername();
        String password = userTestUtils.generatePassword();
        //not register to get failure
        HttpResponse response = authTestUtils.executeLoginByHttpClient(port, username, password);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        Assertions.assertNotNull(responseBody);
        String expected = authTestUtils.mockDefaultBodyWhenLoginFail();
        JSONAssert.assertEquals(expected, responseBody, true);
    }


    @Test
    void testLogin_whenFail_byNullUsername() throws IOException, JSONException, URISyntaxException {
        String password = userTestUtils.generatePassword();
        HttpResponse response = authTestUtils.executeLoginByHttpClient(port, null, password);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        Assertions.assertNotNull(responseBody);
        String expected = authTestUtils.mockBodyWhenValidationException(
                List.of(new ErrorDetailsDTO("username", null, "Blank.user.username")),
                "Error.user.login"
        );
        JSONAssert.assertEquals(expected, responseBody, true);
    }


    @Test
    void testLogin_whenFail_byEmptyUsername() throws IOException, JSONException, URISyntaxException {
        String username = "";
        String password = userTestUtils.generatePassword();
        HttpResponse response = authTestUtils.executeLoginByHttpClient(port, username, password);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        Assertions.assertNotNull(responseBody);
        String expected = authTestUtils.mockBodyWhenValidationException(
                List.of(new ErrorDetailsDTO("username", username, "Blank.user.username")),
                "Error.user.login"
        );
        JSONAssert.assertEquals(expected, responseBody, true);
    }


    @Test
    void testLogin_whenFail_byNullPassword() throws IOException, JSONException, URISyntaxException {
        String username = userTestUtils.generateUsername();
        HttpResponse response = authTestUtils.executeLoginByHttpClient(port, username, null);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        Assertions.assertNotNull(responseBody);
        String expected = authTestUtils.mockBodyWhenValidationException(
                List.of(new ErrorDetailsDTO("password", null, "Blank.user.password")),
                "Error.user.login"
        );
        JSONAssert.assertEquals(expected, responseBody, true);
    }


    @Test
    void testLogin_whenFail_byEmptyPassword() throws IOException, JSONException, URISyntaxException {
        String username = userTestUtils.generateUsername();
        String password = "";
        HttpResponse response = authTestUtils.executeLoginByHttpClient(port, username, password);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        Assertions.assertNotNull(responseBody);
        String expected = authTestUtils.mockBodyWhenValidationException(
                List.of(new ErrorDetailsDTO("password", password, "Blank.user.password")),
                "Error.user.login"
        );
        JSONAssert.assertEquals(expected, responseBody, true);
    }


    @Test
    void testLogin_whenFail_byEmptyBothUsernameAndPassword() throws IOException, JSONException, URISyntaxException {
        String username = "";
        String password = "";
        HttpResponse response = authTestUtils.executeLoginByHttpClient(port, username, password);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        Assertions.assertNotNull(responseBody);
        String expected = authTestUtils.mockBodyWhenValidationException(
                List.of(
                        new ErrorDetailsDTO("username", username, "Blank.user.username"),
                        new ErrorDetailsDTO("password", password, "Blank.user.password")
                ),
                "Error.user.login"
        );
        JSONAssert.assertEquals(expected, responseBody, false);
    }


    @Test
    void testLogin_whenFail_byNullBothUsernameAndPassword() throws IOException, JSONException, URISyntaxException {
        HttpResponse response = authTestUtils.executeLoginByHttpClient(port, null, null);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        Assertions.assertNotNull(responseBody);
        String expected = authTestUtils.mockBodyWhenValidationException(
                List.of(
                        new ErrorDetailsDTO("username", null, "Blank.user.username"),
                        new ErrorDetailsDTO("password", null, "Blank.user.password")
                ),
                "Error.user.login"
        );
        JSONAssert.assertEquals(expected, responseBody, false);
    }

}
