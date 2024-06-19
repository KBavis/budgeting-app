package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.exception.JwtServiceException;
import com.bavis.budgetapp.exception.PlaidServiceException;
import com.bavis.budgetapp.exception.UserServiceException;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.dto.AuthRequestDto;
import com.bavis.budgetapp.dto.AuthResponseDto;
import com.bavis.budgetapp.service.impl.AuthServiceImpl;
import com.bavis.budgetapp.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
@ActiveProfiles(profiles = "test")
public class AuthControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthServiceImpl authService;

    @MockBean
    UserServiceImpl userService;

    @Autowired
    ObjectMapper objectMapper;

    private AuthRequestDto registerAuthRequestDto;
    private AuthRequestDto authenticationAuthRequestDto;

    private AuthResponseDto expectedAuthResponseDto;

    private User testUser;

    @BeforeEach
    public void setup() {
        registerAuthRequestDto = AuthRequestDto.builder()
                .name("Register User")
                .passwordOne("testPassword12!")
                .passwordTwo("testPassword12!")
                .username("register-user")
                .build();

       authenticationAuthRequestDto = AuthRequestDto.builder()
                .username("authentication-user")
                .passwordOne("testPassword12!")
                .build();

       testUser = User.builder()
               .name("Test User")
               .linkToken("link-token")
               .username("test-username")
               .build();

        expectedAuthResponseDto = AuthResponseDto.builder()
                .token("jwt-token")
                .userDetails(testUser)
                .build();
    }

    @Test
    public void testAuthenticate_Successful() throws Exception {
        // Mock
        when(authService.authenticate(any(AuthRequestDto.class))).thenReturn(expectedAuthResponseDto);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authenticationAuthRequestDto)));

        // Assert
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(expectedAuthResponseDto.getToken()))
                .andExpect(jsonPath("$.user.name").value(testUser.getName()))
                .andExpect(jsonPath("$.user.userName").value(testUser.getUsername()))
                .andExpect(jsonPath("$.user.linkToken").value(testUser.getLinkToken()));
        // Verify
        verify(authService, times(1)).authenticate(any(AuthRequestDto.class));
    }

    @Test
    public void testRegister_Successful() throws Exception{
        //Mock
        when(authService.register(any(AuthRequestDto.class))).thenReturn(expectedAuthResponseDto);

        //Act
        ResultActions resultActions = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerAuthRequestDto)));

        //Assert
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(expectedAuthResponseDto.getToken()))
                .andExpect(jsonPath("$.user.name").value(testUser.getName()))
                .andExpect(jsonPath("$.user.userName").value(testUser.getUsername()))
                .andExpect(jsonPath("$.user.linkToken").value(testUser.getLinkToken()));

        verify(authService, times(1)).register(any(AuthRequestDto.class));
    }

    @Test
    public void testAuthenticate_InvalidCredentials_Failure() throws Exception{
        //Mock
        when(authService.authenticate(any(AuthRequestDto.class))).thenThrow(new BadCredentialsException("Bad credentials"));

        //Act
        ResultActions resultActions = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authenticationAuthRequestDto)));

        //Assert
        resultActions.andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Bad credentials"));

        verify(authService, times(1)).authenticate(any(AuthRequestDto.class));
    }

    @Test
    public void testAuthenticate_EmptyUsername_Failure() throws Exception{
        //Arrange
        AuthRequestDto invalidAuthRequestDto = AuthRequestDto.builder()
                .username(null)
                .passwordOne("testPassword12!")
                .build();

        //Act
        ResultActions resultActions = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAuthRequestDto)));

        //Assert
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("The provided username is not in the proper format."));

    }

    @Test
    public void testAuthenticate_EmptyPassword_Failure() throws Exception{
        //Arrange
        AuthRequestDto invalidAuthRequestDto = AuthRequestDto.builder()
                .username("testUsername")
                .passwordOne(null)
                .build();

        //Act
        ResultActions resultActions = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAuthRequestDto)));

        //Assert
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("The provided password is not valid."));

    }

    @Test
    public void testRegister_UsernameInvalid_Failure() throws Exception{
        //Arrange
        AuthRequestDto invalidAuthRequestDto = AuthRequestDto.builder()
                .username("1")
                .passwordOne("validPassword123!")
                .passwordTwo("validPassword123!")
                .name("Valid Name")
                .build();

        //Act
        ResultActions resultActions = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAuthRequestDto)));

        //Assert
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("The provided username is not in the proper format."));

    }

    @Test
    public void testRegister_UsernameTaken_Failure() throws Exception{
        // Mock
        when(userService.existsByUsername("taken-username")).thenReturn(true);

        //Arrange
        AuthRequestDto invalidAuthRequestDto = AuthRequestDto.builder()
                .username("taken-username")
                .passwordOne("validPassword123!")
                .passwordTwo("validPassword123!")
                .name("Valid Name")
                .build();

        //Act
        ResultActions resultActions = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAuthRequestDto)));

        //Assert
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("The provided username already exists."));

        //Verify
        verify(userService, times(1)).existsByUsername("taken-username");
    }

    @Test
    public void testRegister_NameInvalid_Failure() throws Exception{
        //Arrange
        AuthRequestDto invalidAuthRequestDto = AuthRequestDto.builder()
                .username("valid-username")
                .passwordOne("validPassword123!")
                .passwordTwo("validPassword123!")
                .name("123invalidname")
                .build();

        //Act
        ResultActions resultActions = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAuthRequestDto)));

        //Assert
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("The provided name is not valid."));
    }

    @Test
    public void testRegister_PasswordInvalid_Failure() throws Exception{
        //Arrange
        AuthRequestDto invalidAuthRequestDto = AuthRequestDto.builder()
                .username("valid-username")
                .passwordOne("weakpass")
                .passwordTwo("weakpass")
                .name("Valid Name")
                .build();

        //Act
        ResultActions resultActions = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAuthRequestDto)));

        //Assert
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("The provided password is not valid."));
    }

    @Test
    public void testRegister_PasswordMismatch_Failure() throws Exception{
        //Arrange
        AuthRequestDto invalidAuthRequestDto = AuthRequestDto.builder()
                .username("valid-username")
                .passwordOne("passwordValid1!")
                .passwordTwo("passwordValid2!") //different passwords
                .name("Valid Name")
                .build();

        //Act
        ResultActions resultActions = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAuthRequestDto)));

        //Assert
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("The passwords do not match."));
    }

    @Test
    public void testRegister_PlaidServiceException_Failure() throws Exception{
        //Mock
        when(authService.register(any(AuthRequestDto.class))).thenThrow(new PlaidServiceException("Link Token Returned From Client Is Null"));

        //Act
        ResultActions resultActions = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerAuthRequestDto)));

        //Assert
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("PlaidServiceException: [Link Token Returned From Client Is Null]"));
    }

    @Test
    public void testRegister_JwtServiceException_Failure() throws Exception{
        //Mock
        when(authService.register(any(AuthRequestDto.class))).thenThrow(new JwtServiceException("Failed to Generate Jwt Token: invalid usages"));

        //Act
        ResultActions resultActions = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerAuthRequestDto)));

        //Assert
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("JwtServiceException: [Failed to Generate Jwt Token: invalid usages]"));
    }

    @Test
    public void testRegister_UserServiceException_Failure() throws Exception {
        //Mock
        when(authService.register(any(AuthRequestDto.class))).thenThrow(new UserServiceException("Could not find user with the username " + registerAuthRequestDto.getUsername()));

        //Act
        ResultActions resultActions = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerAuthRequestDto)));

        //Assert
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Could not find user with the username " + registerAuthRequestDto.getUsername()));
    }

    @Test
    public void testAuthenticate_UserServiceException_Failure() throws Exception{
        //Mock
        when(authService.authenticate(any(AuthRequestDto.class))).thenThrow(new UserServiceException("Could not find user with the username " + authenticationAuthRequestDto.getUsername()));

        //Act
        ResultActions resultActions = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerAuthRequestDto)));

        //Assert
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Could not find user with the username authentication-user"));
    }

    @Test
    public void testAuthenticate_JwtServiceException_Failure() throws Exception {
        //Mock
        when(authService.authenticate(any(AuthRequestDto.class))).thenThrow(new JwtServiceException("Failed to Generate Jwt Token: invalid usages"));

        //Act
        ResultActions resultActions = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerAuthRequestDto)));

        //Assert
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("JwtServiceException: [Failed to Generate Jwt Token: invalid usages]"));
    }

    @Test
    public void testGetAuthenticatedUser_Success() throws Exception {
        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(testUser);

        //Act & Assert
        mockMvc.perform(get("/auth"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value(testUser.getUsername()))
                .andExpect(jsonPath("$.linkToken").value(testUser.getLinkToken()))
                .andExpect(jsonPath("$.name").value(testUser.getName()));

        //Verify
        verify(userService, times(1)).getCurrentAuthUser();
    }

    @Test
    public void testGetAuthenticatedUser_NoAuthUserFound_Failure() throws Exception {
        //Mock
        when(userService.getCurrentAuthUser()).thenThrow(new UserServiceException("Unable to find any Authenticated user"));

        //Act & Assert
        mockMvc.perform(get("/auth"))
                .andExpect(jsonPath("$.error").value("Unable to find any Authenticated user"));

        //Verify
        verify(userService, times(1)).getCurrentAuthUser();
    }

    @Test
    public void testGetAuthenticatedUser_UsernameNotFound_Failure() throws Exception {
        //Mock
        when(userService.getCurrentAuthUser()).thenThrow(new UserServiceException("Could not find user with the username " + testUser.getUsername()));

        //Act & Assert
        mockMvc.perform(get("/auth"))
                .andExpect(jsonPath("$.error").value("Could not find user with the username " + testUser.getUsername()));

        //Verify
        verify(userService, times(1)).getCurrentAuthUser();
    }

    @Test
    public void testLogout_Successful() throws Exception {
        //Mock
        doNothing().when(authService).logout();

        //Act
        ResultActions resultActions = mockMvc.perform(delete("/auth/logout"));

        //Assert
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        //Verify
        verify(authService, times(1)).logout();
    }

}
