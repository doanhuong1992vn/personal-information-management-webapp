package com.user_service.controller.auth_controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.user_service.UserServiceApplication;
import com.user_service.controller.utils.ErrorDetailsDTO;
import com.user_service.controller.utils.TestUtils;
import com.user_service.exception.CommonError;
import com.user_service.payload.request.LoginRequestDTO;
import com.user_service.payload.response.CommonResponseDTO;
import com.user_service.payload.response.LoginResponseDTO;
import com.user_service.utils.MessageUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginMethodTest {
    @LocalServerPort
    int port;

    @Autowired
    private MessageUtils messageUtils;

    @Autowired
    private TestUtils testUtils;

    TestRestTemplate restTemplate = new TestRestTemplate();

    HttpHeaders headers = new HttpHeaders();

    ObjectMapper objectMapper = new ObjectMapper();


    @BeforeEach
    public void setup() {
        objectMapper.registerModule(new JavaTimeModule());
    }


    ResponseEntity<String> executeLoginByTestRestTemplate(String username, String password) {
        LoginRequestDTO body = new LoginRequestDTO(username, password);
        HttpEntity<LoginRequestDTO> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(
                testUtils.getLoginURL(port), HttpMethod.POST, entity, String.class
        );
    }


    @Test
    void testLogin_whenSuccess() throws JsonProcessingException, JSONException {
        String username = "testLogin1";
        String password = "Glsoft@123";
        testUtils.executeRegister(port, username, password);
        ResponseEntity<String> response = executeLoginByTestRestTemplate(username, password);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        String token = getTokenAndValidate(response);
        String expected = mockBodyWhenLoginSuccess(username, token);
        JSONAssert.assertEquals(expected, response.getBody(), true);
    }


    String mockBodyWhenLoginSuccess(String username, String token) throws JsonProcessingException {
        LoginResponseDTO mockData = new LoginResponseDTO(username, token, null);
        CommonResponseDTO mockBody = new CommonResponseDTO(
                true, messageUtils.getMessage("Success.user.login"), mockData
        );
        return objectMapper.writeValueAsString(mockBody);
    }


    String getTokenAndValidate(ResponseEntity<String> response) throws JsonProcessingException {
        TypeReference<CommonResponseDTO> typeReference = new TypeReference<>() {};
        CommonResponseDTO responseBody = objectMapper.readValue(response.getBody(), typeReference);
        Assertions.assertNotNull(responseBody);
        LinkedHashMap<String, String> responseData = (LinkedHashMap) responseBody.data();
        String token = responseData.get("token");
        Assertions.assertNotNull(token);
        Assertions.assertFalse(token.isEmpty());
        return token;
    }


    @Test
    void testLogin_whenFail_byWrongUsername() throws IOException, JSONException {
        String username = "wrongUsername";
        String password = "Glsoft@123";
        testUtils.executeRegister(port, username, password);
        HttpResponse response = testUtils.executeLoginByHttpClient(port, username + "123", password);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        Assertions.assertNotNull(responseBody);
        String expected = mockDefaultBodyWhenLoginFail();
        JSONAssert.assertEquals(expected, responseBody, true);
    }


    @Test
    void testLogin_whenFail_byWrongPassword() throws IOException, JSONException {
        String username = "wrongPassword";
        String password = "Glsoft@123";
        testUtils.executeRegister(port, username, password);
        HttpResponse response = testUtils.executeLoginByHttpClient(port, username, password + "@123");
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        Assertions.assertNotNull(responseBody);
        String expected = mockDefaultBodyWhenLoginFail();
        JSONAssert.assertEquals(expected, responseBody, true);
    }


    @Test
    void testLogin_whenFail_byWrongBothUsernameAndPassword() throws IOException, JSONException {
        String username = RandomStringUtils.randomAlphabetic(8, 20);
        String password = RandomStringUtils.randomAlphabetic(8, 20);
        System.out.println("Mock username: " + username);
        System.out.println("Mock password: " + password);
        HttpResponse response = testUtils.executeLoginByHttpClient(port, username, password);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        Assertions.assertNotNull(responseBody);
        String expected = mockDefaultBodyWhenLoginFail();
        JSONAssert.assertEquals(expected, responseBody, true);
    }

    String mockDefaultBodyWhenLoginFail() throws JsonProcessingException {
        CommonError mockBody = new CommonError(
                messageUtils.getMessage("Error.user.login"),
                messageUtils.getMessage("Error.user.login.field")
        );
        return objectMapper.writeValueAsString(mockBody);
    }

    @Test
    void testLogin_whenFail_byNullUsername() throws IOException, JSONException {
        String password = "Glsoft@123";
        HttpResponse response = testUtils.executeLoginByHttpClient(port, null, password);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        Assertions.assertNotNull(responseBody);
        String expected = testUtils.mockConstraintValidationBody(
                List.of(new ErrorDetailsDTO("username", null, "Blank.user.username")),
                "Error.user.login"
        );
        JSONAssert.assertEquals(expected, responseBody, true);
    }


    @Test
    void testLogin_whenFail_byEmptyUsername() throws IOException, JSONException {
        String username = "";
        String password = "Glsoft@123";
        HttpResponse response = testUtils.executeLoginByHttpClient(port, username, password);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        Assertions.assertNotNull(responseBody);
        String expected = testUtils.mockConstraintValidationBody(
                List.of(new ErrorDetailsDTO("username", username, "Blank.user.username")),
                "Error.user.login"
        );
        JSONAssert.assertEquals(expected, responseBody, true);
    }


    @Test
    void testLogin_whenFail_byNullPassword() throws IOException, JSONException {
        String username = "nullPassword";
        HttpResponse response = testUtils.executeLoginByHttpClient(port, username, null);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        Assertions.assertNotNull(responseBody);
        String expected = testUtils.mockConstraintValidationBody(
                List.of(new ErrorDetailsDTO("password", null, "Blank.user.password")),
                "Error.user.login"
        );
        JSONAssert.assertEquals(expected, responseBody, true);
    }


    @Test
    void testLogin_whenFail_byEmptyPassword() throws IOException, JSONException {
        String username = "nullPassword";
        String password = "";
        HttpResponse response = testUtils.executeLoginByHttpClient(port, username, password);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        Assertions.assertNotNull(responseBody);
        String expected = testUtils.mockConstraintValidationBody(
                List.of(new ErrorDetailsDTO("password", password, "Blank.user.password")),
                "Error.user.login"
        );
        JSONAssert.assertEquals(expected, responseBody, true);
    }


    @Test
    void testLogin_whenFail_byEmptyBothUsernameAndPassword() throws IOException, JSONException {
        String username = "";
        String password = "";
        HttpResponse response = testUtils.executeLoginByHttpClient(port, username, password);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        Assertions.assertNotNull(responseBody);
        String expected = testUtils.mockConstraintValidationBody(
                List.of(
                        new ErrorDetailsDTO("username", username, "Blank.user.username"),
                        new ErrorDetailsDTO("password", password, "Blank.user.password")
                ),
                "Error.user.login"
        );
        JSONAssert.assertEquals(expected, responseBody, false);
    }


    @Test
    void testLogin_whenFail_byNullBothUsernameAndPassword() throws IOException, JSONException {
        HttpResponse response = testUtils.executeLoginByHttpClient(port, null, null);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        Assertions.assertNotNull(responseBody);
        String expected = testUtils.mockConstraintValidationBody(
                List.of(
                        new ErrorDetailsDTO("username", null, "Blank.user.username"),
                        new ErrorDetailsDTO("password", null, "Blank.user.password")
                ),
                "Error.user.login"
        );
        JSONAssert.assertEquals(expected, responseBody, false);
    }














}
