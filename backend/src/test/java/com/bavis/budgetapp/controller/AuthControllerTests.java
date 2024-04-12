package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.request.AuthRequest;
import com.bavis.budgetapp.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class AuthControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthService authService;

    @Autowired
    ObjectMapper objectMapper;

    private AuthRequest registerAuthRequest;
    private AuthRequest authenticationAuthRequest;

    @BeforeEach
    public void setup() {
        registerAuthRequest = AuthRequest.builder()
                .name("Register User")
                .passwordOne("test")
                .passwordTwo("test")
                .username("register-user")
                .build();

       authenticationAuthRequest = AuthRequest.builder()
                .username("authentication-user")
                .passwordOne("test")
                .build();
    }

    @Test
    public void testAuthenticate_Successful() {
        //Mock
    }

    //TODO: test responses based on input dto validation errors and errors when registering/connecting account
}
