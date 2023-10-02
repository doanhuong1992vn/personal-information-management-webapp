package com.user_service.controller.auth_controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.user_service.UserServiceApplication;
import com.user_service.controller.dto.ErrorDetailsDTO;
import com.user_service.controller.utils.AuthTestUtils;
import com.user_service.exception.CommonError;
import com.user_service.formater.TimeFormatter;
import com.user_service.payload.response.CommonResponseDTO;
import com.user_service.payload.response.RegisterResponseDTO;
import com.user_service.utils.MessageUtils;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegisterMethodTest {
    @LocalServerPort
    int port;

    @Autowired
    private MessageUtils messageUtils;

    @Autowired
    private TimeFormatter timeFormatter;

    @Autowired
    private AuthTestUtils authTestUtils;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        objectMapper.registerModule(new JavaTimeModule());
    }


    @Test
    void testRegister_whenSuccess() throws JSONException, JsonProcessingException {
        String username = "testSuccess1";
        String password = "Glsoft@123";
        LocalDate birthday = LocalDate.parse("1992-02-18");
        ResponseEntity<String> response = authTestUtils.executeRegister(port, username, password, birthday);
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        CommonResponseDTO mockBody = new CommonResponseDTO(
                true,
                messageUtils.getMessage("Success.user.register"),
                new RegisterResponseDTO(username, timeFormatter.format(birthday))
        );
        String expected = objectMapper.writeValueAsString(mockBody);
        JSONAssert.assertEquals(expected, response.getBody(), true);
    }




    @Test
    void testRegister_whenThrowDuplicateUsernameException() throws JSONException, JsonProcessingException {
        String username = "duplicate";
        String password = "Glsoft@123";
        authTestUtils.executeRegister(port, username, password);
        ResponseEntity<String> response = authTestUtils.executeRegister(port, username, password);
        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        CommonError mockBody = new CommonError(false,
                messageUtils.getMessage("Error.user.register.duplication"),
                messageUtils.getMessage("Error.user.username.exists", username)
        );
        String expected = objectMapper.writeValueAsString(mockBody);
        JSONAssert.assertEquals(expected, response.getBody(), true);
    }


    @Test
    void testRegister_whenThrowValidationException_byEmptyUsername()
            throws JsonProcessingException, JSONException {
        String username = "";
        String password = "Glsoft@123";
        ResponseEntity<String> response = authTestUtils.executeRegister(port, username, password);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String expected = authTestUtils.mockBodyWhenValidationException(
                List.of(
                        new ErrorDetailsDTO("username", username, "Blank.user.username"),
                        new ErrorDetailsDTO("username", username, "Size.user.username"),
                        new ErrorDetailsDTO("username", username, "Pattern.user.username.regex")
                ),
                "Error.user.register.validation"
        );
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }


    @Test
    void testRegister_whenThrowValidationException_byNullUsername()
            throws JsonProcessingException, JSONException {
        String password = "Glsoft@123";
        ResponseEntity<String> response = authTestUtils.executeRegister(port, null, password);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String expected = authTestUtils.mockBodyWhenValidationException(
                List.of(new ErrorDetailsDTO("username", null, "Blank.user.username")),
                "Error.user.register.validation"
        );
        JSONAssert.assertEquals(expected, response.getBody(), true);
    }


    @Test
    void testRegister_whenThrowValidationException_byUsernameContainsSpecialCharacters()
            throws JsonProcessingException, JSONException {
        String username = "username@";
        String password = "Glsoft@123";
        ResponseEntity<String> response = authTestUtils.executeRegister(port, username, password);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String expected = authTestUtils.mockBodyWhenValidationException(
                List.of(new ErrorDetailsDTO("username", username, "Pattern.user.username.regex")),
                "Error.user.register.validation"
        );
        JSONAssert.assertEquals(expected, response.getBody(), true);
    }


    @Test
    void testRegister_whenThrowValidationException_byUsernameLessThan8Characters()
            throws JsonProcessingException, JSONException {
        String username = "user";
        String password = "Glsoft@123";
        ResponseEntity<String> response = authTestUtils.executeRegister(port, username, password);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String expected = authTestUtils.mockBodyWhenValidationException(
                List.of(new ErrorDetailsDTO("username", username, "Size.user.username")),
                "Error.user.register.validation"
        );
        JSONAssert.assertEquals(expected, response.getBody(), true);
    }


    @Test
    void testRegister_whenThrowValidationException_byUsernameMoreThan20Characters()
            throws JsonProcessingException, JSONException {
        String username = "username12345678901234567890";
        String password = "Glsoft@123";
        ResponseEntity<String> response = authTestUtils.executeRegister(port, username, password);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String expected = authTestUtils.mockBodyWhenValidationException(
                List.of(new ErrorDetailsDTO("username", username, "Size.user.username")),
                "Error.user.register.validation"
        );
        JSONAssert.assertEquals(expected, response.getBody(), true);
    }


    @Test
    void testRegister_whenThrowValidationException_byEmptyPassword() throws JsonProcessingException, JSONException {
        String username = "username1";
        String password = "";
        ResponseEntity<String> response = authTestUtils.executeRegister(port, username, password);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String expected = authTestUtils.mockBodyWhenValidationException(
                List.of(
                        new ErrorDetailsDTO("password", password, "Blank.user.password"),
                        new ErrorDetailsDTO("password", password, "Size.user.password"),
                        new ErrorDetailsDTO("password", password, "Pattern.user.password.special-characters"),
                        new ErrorDetailsDTO("password", password, "Pattern.user.password.number"),
                        new ErrorDetailsDTO("password", password, "Pattern.user.password.uppercase-letters"),
                        new ErrorDetailsDTO("password", password, "Pattern.user.password.lowercase-letters")
                ),
                "Error.user.register.validation"
        );
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }


    @Test
    void testRegister_whenThrowValidationException_byNullPassword() throws JsonProcessingException, JSONException {
        String username = "username1";
        ResponseEntity<String> response = authTestUtils.executeRegister(port, username, null);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String expected = authTestUtils.mockBodyWhenValidationException(
                List.of(new ErrorDetailsDTO("password", null, "Blank.user.password")),
                "Error.user.register.validation"
        );
        JSONAssert.assertEquals(expected, response.getBody(), true);
    }


    @Test
    void testRegister_whenThrowValidationException_byPasswordLessThan8Characters() throws JsonProcessingException, JSONException {
        String username = "username1";
        String password = "Abc@12";
        ResponseEntity<String> response = authTestUtils.executeRegister(port, username, password);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String expected = authTestUtils.mockBodyWhenValidationException(
                List.of(new ErrorDetailsDTO("password", password, "Size.user.password")),
                "Error.user.register.validation"
        );
        JSONAssert.assertEquals(expected, response.getBody(), true);
    }


    @Test
    void testRegister_whenThrowValidationException_byPasswordMoreThan20Characters() throws JsonProcessingException, JSONException {
        String username = "username1";
        String password = "Glsoft@12345678901234567890";
        ResponseEntity<String> response = authTestUtils.executeRegister(port, username, password);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String expected = authTestUtils.mockBodyWhenValidationException(
                List.of(new ErrorDetailsDTO("password", password, "Size.user.password")),
                "Error.user.register.validation"
        );
        JSONAssert.assertEquals(expected, response.getBody(), true);
    }


    @Test
    void testRegister_whenThrowValidationException_byPasswordNotContainSpecialCharacter() throws JsonProcessingException, JSONException {
        String username = "username1";
        String password = "Glsoft123";
        ResponseEntity<String> response = authTestUtils.executeRegister(port, username, password);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String expected = authTestUtils.mockBodyWhenValidationException(
                List.of(new ErrorDetailsDTO("password", password, "Pattern.user.password.special-characters")),
                "Error.user.register.validation"
        );
        JSONAssert.assertEquals(expected, response.getBody(), true);
    }


    @Test
    void testRegister_whenThrowValidationException_byPasswordNotContainNumber() throws JsonProcessingException, JSONException {
        String username = "username1";
        String password = "Glsoft@#$%";
        ResponseEntity<String> response = authTestUtils.executeRegister(port, username, password);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String expected = authTestUtils.mockBodyWhenValidationException(
                List.of(new ErrorDetailsDTO("password", password, "Pattern.user.password.number")),
                "Error.user.register.validation"
        );
        JSONAssert.assertEquals(expected, response.getBody(), true);
    }


    @Test
    void testRegister_whenThrowValidationException_byPasswordNotContainUppercaseLetters() throws JsonProcessingException, JSONException {
        String username = "username1";
        String password = "glsoft@123";
        ResponseEntity<String> response = authTestUtils.executeRegister(port, username, password);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String expected = authTestUtils.mockBodyWhenValidationException(
                List.of(new ErrorDetailsDTO("password", password, "Pattern.user.password.uppercase-letters")),
                "Error.user.register.validation"
        );
        JSONAssert.assertEquals(expected, response.getBody(), true);
    }


    @Test
    void testRegister_whenThrowValidationException_byPasswordNotContainLowercaseLetters() throws JsonProcessingException, JSONException {
        String username = "username1";
        String password = "GLSOFT@123";
        ResponseEntity<String> response = authTestUtils.executeRegister(port, username, password);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String expected = authTestUtils.mockBodyWhenValidationException(
                List.of(new ErrorDetailsDTO("password", password, "Pattern.user.password.lowercase-letters")),
                "Error.user.register.validation"
        );
        JSONAssert.assertEquals(expected, response.getBody(), true);
    }


    @Test
    void testRegister_whenBirthdayIsFuture() throws JsonProcessingException, JSONException {
        String username = "username1";
        String password = "Glsoft@123";
        LocalDate birthday = LocalDate.now().plusDays(1);
        ResponseEntity<String> response = authTestUtils.executeRegister(port, username, password, birthday);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String expected = authTestUtils.mockBodyWhenValidationException(
                List.of(new ErrorDetailsDTO("birthday", birthday.toString(), "Past.user.birthday")),
                "Error.user.register.validation"
        );
        JSONAssert.assertEquals(expected, response.getBody(), true);
    }


    @Test
    void testRegister_whenUserMoreThan100YearsOld() throws JsonProcessingException, JSONException {
        String username = "username1";
        String password = "Glsoft@123";
        LocalDate birthday = LocalDate.now().minusYears(101);
        ResponseEntity<String> response = authTestUtils.executeRegister(port, username, password, birthday);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String expected = authTestUtils.mockBodyWhenValidationException(
                List.of(new ErrorDetailsDTO("birthday", birthday.toString(), "TooOld.user.birthday")),
                "Error.user.register.validation"
        );
        JSONAssert.assertEquals(expected, response.getBody(), true);
    }
}