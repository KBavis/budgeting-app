package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.AccountDTO;
import com.bavis.budgetapp.model.User;
import com.bavis.budgetapp.request.AuthRequest;
import com.bavis.budgetapp.response.AuthResponse;
import com.bavis.budgetapp.service.AuthService;
import com.bavis.budgetapp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class AuthControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthService authService;

    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    private AuthRequest registerAuthRequest;
    private AuthRequest authenticationAuthRequest;

    private AccountDTO accountDTO;

    private AuthResponse expectedAuthResponse;

    private User testUser;

    @BeforeEach
    public void setup() {
        registerAuthRequest = AuthRequest.builder()
                .name("Register User")
                .passwordOne("testPassword12!")
                .passwordTwo("testPassword12!")
                .username("register-user")
                .build();

       authenticationAuthRequest = AuthRequest.builder()
                .username("authentication-user")
                .passwordOne("testPassword12!")
                .build();

       testUser = User.builder()
               .name("Test User")
               .linkToken("link-token")
               .username("test-username")
               .build();

        expectedAuthResponse = AuthResponse.builder()
                .token("jwt-token")
                .userDetails(testUser)
                .build();
    }

    @Test
    public void testAuthenticate_Successful() throws Exception {
        // Mock
        when(authService.authenticate(any(AuthRequest.class))).thenReturn(expectedAuthResponse);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authenticationAuthRequest)));

        // Assert
        resultActions.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(expectedAuthResponse.getToken()))
                .andExpect(jsonPath("$.user.name").value(testUser.getName()))
                .andExpect(jsonPath("$.user.userName").value(testUser.getUsername()))
                .andExpect(jsonPath("$.user.linkToken").value(testUser.getLinkToken()));
        // Verify
        verify(authService, times(1)).authenticate(any(AuthRequest.class));
    }

    @Test
    public void testRegister_Successful() throws Exception{
        //Mock
        when(authService.register(any(AuthRequest.class))).thenReturn(expectedAuthResponse);

        //Act
        ResultActions resultActions = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerAuthRequest)));

        //Assert
        resultActions.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(expectedAuthResponse.getToken()))
                .andExpect(jsonPath("$.user.name").value(testUser.getName()))
                .andExpect(jsonPath("$.user.userName").value(testUser.getUsername()))
                .andExpect(jsonPath("$.user.linkToken").value(testUser.getLinkToken()));

        verify(authService, times(1)).register(any(AuthRequest.class));
    }

    @Test
    public void testAuthenticate_InvalidCredentials_Failure() throws Exception{
        //Mock
        when(authService.authenticate(any(AuthRequest.class))).thenThrow(new BadCredentialsException("Bad credentials"));

        //Act
        ResultActions resultActions = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authenticationAuthRequest)));

        //Assert
        resultActions.andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Bad credentials"));

        verify(authService, times(1)).authenticate(any(AuthRequest.class));
    }

    @Test
    public void testAuthenticate_EmptyUsername_Failure() throws Exception{
        //Arrange
        AuthRequest invalidAuthRequest = AuthRequest.builder()
                .username(null)
                .passwordOne("testPassword12!")
                .build();

        //Act
        ResultActions resultActions = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAuthRequest)));

        //Assert
        resultActions.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("The provided username is not in the proper format."));

    }

    @Test
    public void testAuthenticate_EmptyPassword_Failure() throws Exception{
        //Arrange
        AuthRequest invalidAuthRequest = AuthRequest.builder()
                .username("testUsername")
                .passwordOne(null)
                .build();

        //Act
        ResultActions resultActions = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAuthRequest)));

        //Assert
        resultActions.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("The provided password is not valid."));

    }

    @Test
    public void testRegister_UsernameInvalid_Failure() throws Exception{
        //Arrange
        AuthRequest invalidAuthRequest = AuthRequest.builder()
                .username("1")
                .passwordOne("validPassword123!")
                .passwordTwo("validPassword123!")
                .name("Valid Name")
                .build();

        //Act
        ResultActions resultActions = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAuthRequest)));

        //Assert
        resultActions.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("The provided username is not in the proper format."));

    }

    @Test
    public void testRegister_UsernameTaken_Failure() throws Exception{
        // Mock
        when(userService.existsByUsername("taken-username")).thenReturn(true);

        //Arrange
        AuthRequest invalidAuthRequest = AuthRequest.builder()
                .username("taken-username")
                .passwordOne("validPassword123!")
                .passwordTwo("validPassword123!")
                .name("Valid Name")
                .build();

        //Act
        ResultActions resultActions = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAuthRequest)));

        //Assert
        resultActions.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("The provided username already exists."));

        //Verify
        verify(userService, times(1)).existsByUsername("taken-username");
    }

    @Test
    public void testRegister_NameInvalid_Failure() throws Exception{
        //Arrange
        AuthRequest invalidAuthRequest = AuthRequest.builder()
                .username("valid-username")
                .passwordOne("validPassword123!")
                .passwordTwo("validPassword123!")
                .name("123invalidname")
                .build();

        //Act
        ResultActions resultActions = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAuthRequest)));

        //Assert
        resultActions.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("The provided name is not valid."));
    }

    @Test
    public void testRegister_PasswordInvalid_Failure() throws Exception{
        //Arrange
        AuthRequest invalidAuthRequest = AuthRequest.builder()
                .username("valid-username")
                .passwordOne("weakpass")
                .passwordTwo("weakpass")
                .name("Valid Name")
                .build();

        //Act
        ResultActions resultActions = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAuthRequest)));

        //Assert
        resultActions.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("The provided password is not valid."));
    }

    @Test
    public void testRegister_PasswordMismatch_Failure() throws Exception{
        //Arrange
        AuthRequest invalidAuthRequest = AuthRequest.builder()
                .username("valid-username")
                .passwordOne("passwordValid1!")
                .passwordTwo("passwordValid2!") //different passwords
                .name("Valid Name")
                .build();

        //Act
        ResultActions resultActions = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAuthRequest)));

        //Assert
        resultActions.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("The passwords do not match."));
    }

    @Test
    public void testRegister_PlaidServiceException_Failure() {

    }

    @Test
    public void testRegister_JwtServiceException_Failure() {

    }

    @Test
    public void testAuthenticate_JwtServiceException_Failure() {

    }
    //todo: add test cases for handling external service exceptions (Plaid, JWT, User, etc)

}
