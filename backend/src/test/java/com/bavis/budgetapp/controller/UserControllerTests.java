package com.bavis.budgetapp.controller;


import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.exception.PlaidServiceException;
import com.bavis.budgetapp.exception.UserServiceException;
import com.bavis.budgetapp.model.LinkToken;
import com.bavis.budgetapp.service.impl.PlaidServiceImpl;
import com.bavis.budgetapp.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
@ActiveProfiles(profiles = "test")
public class UserControllerTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private PlaidServiceImpl plaidService;


    @Test
    void testRefreshLinkToken_Successful() throws Exception{
        //Arrange
        String oldToken = "old-token";
        LocalDateTime oldExpiration = LocalDateTime.now().minusDays(1);
        LocalDateTime newExpiration = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
        String newToken = "new-token";
        LinkToken oldLinkToken = LinkToken.builder()
                .token(oldToken)
                .expiration(oldExpiration)
                .build();
        LinkToken newLinkToken = LinkToken.builder()
                .expiration(newExpiration)
                .token(newToken)
                .build();
        User oldUser = User.builder()
                .linkToken(oldLinkToken)
                .userId(10L)
                .username("username")
                .build();

        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(oldUser);
        when(plaidService.generateLinkToken(oldUser.getUserId())).thenReturn(newLinkToken);
        when(userService.update(any(Long.class), any(User.class))).thenReturn(oldUser);

        //Act & Assert
        mockMvc.perform(put("/user/refresh/link-token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(newToken))
                .andExpect(jsonPath("$.expiration").value(newExpiration.toString()));

        //Verify
        verify(userService, times(1)).update(any(Long.class), any(User.class));
        verify(plaidService, times(1)).generateLinkToken(oldUser.getUserId());
        verify(userService, times(1)).getCurrentAuthUser();
    }

    @Test
    void testRefreshLinkToken_UserServiceException_Failure() throws Exception{
        //Arrange
        UserServiceException expectedException = new UserServiceException("User with the ID 4 not found");

        //Mock
        when(userService.getCurrentAuthUser()).thenThrow(expectedException);

        //Act & Assert
        mockMvc.perform(put("/user/refresh/link-token"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(expectedException.getMessage()));

        //Verify
        verify(userService, times(1)).getCurrentAuthUser();
    }

    @Test
    void testRefreshLinkToken_PlaidServiceException_Failure() throws Exception{
        //Arrange
        PlaidServiceException expectedException = new PlaidServiceException("Unable to generate link token");
        User user = User.builder()
                .userId(10L)
                .build();

        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(plaidService.generateLinkToken(user.getUserId())).thenThrow(expectedException);

        //Act & Assert
        mockMvc.perform(put("/user/refresh/link-token"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(expectedException.getMessage()));

        //Verify
        verify(userService, times(1)).getCurrentAuthUser();
        verify(plaidService, times(1)).generateLinkToken(user.getUserId());
    }
}
