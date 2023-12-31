package com.user_service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.user_service.payload.request.LoginRequestDTO;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.URI;

public class ApiUtils {
    public static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    public static String registerURL(int port) {
        return API.HOST + port + API.REGISTER;
    }


    public static String loginURL(int port) {
        return API.HOST + port + API.LOGIN;
    }


    public static String logoutURL(int port) {
        return API.HOST + port + API.LOGOUT;
    }


    public static String userURL(int port, String username) {
        return API.HOST + port + API.USER + username;
    }


    public static String checkUsernameURL(int port, String username) {
        return API.HOST + port + API.CHECK_USERNAME + username;
    }


    public static HttpResponse execute(
            HttpEntityEnclosingRequestBase http, URI uri, String token, Object body
    ) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        http.setURI(uri);
        http.setHeader("Content-Type", "application/json");
        http.setHeader("Authorization", "Bearer " + token);
        String entity = objectMapper.writeValueAsString(body);
        StringEntity requestBody = new StringEntity(entity);
        http.setEntity(requestBody);
        return httpClient.execute(http);
    }


    public static ResponseEntity<String> execute(String url, HttpMethod httpMethod, String token, Object body) {
        TestRestTemplate restTemplate = new TestRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(url, httpMethod, entity, String.class);
    }


    public static ResponseEntity<String> execute(String url, HttpMethod httpMethod, Object body) {
        return execute(url, httpMethod, null, body);
    }
}
