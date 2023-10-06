package com.user_service.controller.user_controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user_service.UserServiceApplication;
import com.user_service.exception.CommonError;
import com.user_service.exception.ErrorDetails;
import com.user_service.formater.TimeFormatter;
import com.user_service.payload.request.UserRequestDTO;
import com.user_service.utils.ApiUtils;
import com.user_service.utils.AuthTestUtils;
import com.user_service.utils.MessageSrc;
import com.user_service.utils.UserTestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UpdateProfileApiTest {
    @LocalServerPort
    int port;

    @Autowired
    private AuthTestUtils authTestUtils;

    @Autowired
    private UserTestUtils userTestUtils;

    @Autowired
    private TimeFormatter timeFormatter;

    @Autowired
    private MessageSrc messageSrc;

    @Test
    void testUpdateProfile_whenSuccess() throws JsonProcessingException {
        String username = userTestUtils.generateUsername();
        String password = userTestUtils.generatePassword();
        String token = authTestUtils.getTokenAfterLogin(port, username, password);
        LocalDate birthday = LocalDate.parse("1992-02-18");
        UserRequestDTO body = new UserRequestDTO(birthday);
        ResponseEntity<String> response = userTestUtils.executeUserAPI(port, username, HttpMethod.PUT, token, body);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        LinkedHashMap<String, String> data = authTestUtils.getResponseData(response);
        Assertions.assertNotNull(data);
        Assertions.assertEquals(username, data.get("username"));
        Assertions.assertEquals(timeFormatter.format(birthday), data.get("birthday"));
        Assertions.assertNotNull(data.get("lastLogin"));
        Assertions.assertNotNull(data.get("createTime"));
    }

    @Test
    void testUpdateProfile_whenFail_byUsingUsernameOfOtherInURI() throws IOException, URISyntaxException {
        String usernameOfOther = userTestUtils.generateUsername();
        String passwordOfOther = userTestUtils.generatePassword();
        authTestUtils.executeRegister(port, usernameOfOther, passwordOfOther);
        String username = userTestUtils.generateUsername();
        String password = userTestUtils.generatePassword();
        String token = authTestUtils.getTokenAfterLogin(port, username, password);
        UserRequestDTO body = new UserRequestDTO(LocalDate.parse("1992-02-18"));
        HttpResponse response = ApiUtils.execute(new HttpPut(), new URI(ApiUtils.userURL(port, usernameOfOther)), token, body);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusLine().getStatusCode());
        Assertions.assertNotNull(response.getEntity());
    }

    @Test
    void testUpdateProfile_whenFail_byNullNewBirthday() throws JsonProcessingException, JSONException {
        String username = userTestUtils.generateUsername();
        String password = userTestUtils.generatePassword();
        String token = authTestUtils.getTokenAfterLogin(port, username, password);
        UserRequestDTO body = new UserRequestDTO(null);
        ResponseEntity<String> response = userTestUtils.executeUserAPI(port, username, HttpMethod.PUT, token, body);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        CommonError mockBody = new CommonError(
                false,
                messageSrc.getMessage("Error.user.update"),
                List.of(new ErrorDetails("birthday", null, messageSrc.getMessage("Blank.user.newBirthday")))
        );
        String expected = new ObjectMapper().writeValueAsString(mockBody);
        JSONAssert.assertEquals(expected, response.getBody(), true);
    }


}
