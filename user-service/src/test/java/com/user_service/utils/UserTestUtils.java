package com.user_service.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UserTestUtils {

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
            int port, String username, HttpMethod httpMethod, String token, Object body
    ) {
        return ApiUtils.execute(ApiUtils.userURL(port, username), httpMethod, token, body);
    }


}
