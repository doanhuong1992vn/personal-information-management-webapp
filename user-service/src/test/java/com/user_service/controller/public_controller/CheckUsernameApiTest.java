package com.user_service.controller.public_controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user_service.UserServiceApplication;
import com.user_service.payload.response.CheckerResponseDTO;
import com.user_service.payload.response.CommonResponseDTO;
import com.user_service.utils.ApiUtils;
import com.user_service.utils.AuthTestUtils;
import com.user_service.utils.MessageSrc;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CheckUsernameApiTest {
    @LocalServerPort
    int port;

    @Autowired
    private AuthTestUtils authTestUtils;

    @Autowired
    private UserTestUtils userTestUtils;

    @Autowired
    private MessageSrc messageSrc;

    private static final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void testCheckUserName_byExistUsername() throws JsonProcessingException {
        String username = userTestUtils.generateUsername();
        String password = userTestUtils.generatePassword();
        authTestUtils.executeRegister(port, username, password);
        ResponseEntity<String> response = ApiUtils.execute(ApiUtils.checkUsernameURL(port, username), HttpMethod.GET, null);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        TypeReference<CommonResponseDTO> bodyReference = new TypeReference<>(){};
        CommonResponseDTO body = objectMapper.readValue(response.getBody(), bodyReference);
        Assertions.assertNotNull(body);
        Assertions.assertTrue(body.success());
        String strData = objectMapper.writeValueAsString(body.data());
        TypeReference<CheckerResponseDTO> dataReference = new TypeReference<>(){};
        CheckerResponseDTO data = objectMapper.readValue(strData, dataReference);
        Assertions.assertTrue(data.result());
        Assertions.assertEquals(messageSrc.getMessage("Exists.username.true", username), data.description());
    }


    @Test
    void testCheckUserName_byNotExistUsername() throws JsonProcessingException {
        String username = userTestUtils.generateUsername();
        ResponseEntity<String> response = ApiUtils.execute(ApiUtils.checkUsernameURL(port, username), HttpMethod.GET, null);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        TypeReference<CommonResponseDTO> bodyReference = new TypeReference<>(){};
        CommonResponseDTO body = objectMapper.readValue(response.getBody(), bodyReference);
        Assertions.assertNotNull(body);
        Assertions.assertTrue(body.success());
        String strData = objectMapper.writeValueAsString(body.data());
        TypeReference<CheckerResponseDTO> dataReference = new TypeReference<>(){};
        CheckerResponseDTO data = objectMapper.readValue(strData, dataReference);
        Assertions.assertFalse(data.result());
        Assertions.assertEquals(messageSrc.getMessage("Exists.username.false", username), data.description());
    }
}
