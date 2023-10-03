package com.user_service.controller.user_controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user_service.UserServiceApplication;
import com.user_service.payload.request.PasswordRequestDTO;
import com.user_service.payload.response.CommonResponseDTO;
import com.user_service.utils.ApiUtils;
import com.user_service.utils.AuthTestUtils;
import com.user_service.utils.MessageSrc;
import com.user_service.utils.UserTestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPatch;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UpdatePasswordApiTest {
    @LocalServerPort
    int port;

    @Autowired
    private AuthTestUtils authTestUtils;

    @Autowired
    private UserTestUtils userTestUtils;

    @Autowired
    private MessageSrc messageSrc;


    @Test
    void testUpdate_whenSuccess() throws IOException, JSONException, URISyntaxException {
        String username = userTestUtils.generateUsername();
        String oldPassword = userTestUtils.generatePassword();
        String token = authTestUtils.getTokenAfterLogin(port, username, oldPassword);
        String newPassword = userTestUtils.generatePassword();
        PasswordRequestDTO body = new PasswordRequestDTO(oldPassword, newPassword);
        HttpResponse response = ApiUtils.execute(new HttpPatch(), new URI(ApiUtils.userURL(port, username)), token, body);
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        CommonResponseDTO mockBody = new CommonResponseDTO(
                true, messageSrc.getMessage("Success.user.update"), null
        );
        String expected = new ObjectMapper().writeValueAsString(mockBody);
        JSONAssert.assertEquals(expected, responseBody, true);
    }

    @Test
    void testUpdate_whenFail_byWrongOldPassword() throws IOException, JSONException, URISyntaxException {
        String username = userTestUtils.generateUsername();
        String oldPassword = userTestUtils.generatePassword();
        String token = authTestUtils.getTokenAfterLogin(port, username, oldPassword);
        String newPassword = userTestUtils.generatePassword();
        PasswordRequestDTO body = new PasswordRequestDTO(newPassword, newPassword);
        HttpResponse response = ApiUtils.execute(new HttpPatch(), new URI(ApiUtils.userURL(port, username)), token, body);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        CommonResponseDTO mockBody = new CommonResponseDTO(
                false, messageSrc.getMessage("Error.user.password.update"), null
        );
        String expected = new ObjectMapper().writeValueAsString(mockBody);
        JSONAssert.assertEquals(expected, responseBody, true);
    }

    //We tested all cases of invalid password validation in RegisterApiTest
}
