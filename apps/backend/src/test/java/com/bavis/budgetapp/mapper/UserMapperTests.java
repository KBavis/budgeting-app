package com.bavis.budgetapp.mapper;


import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.model.LinkToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ContextConfiguration(classes = {UserMapperImpl.class})
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
public class UserMapperTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testUpdateUserProfile_Successful() {
        //Arrange
        LocalDateTime expiration = LocalDateTime.now();
        LinkToken linkToken = LinkToken.builder()
                .token("token")
                .expiration(expiration)
                .build();
        User source = User.builder()
                .userId(10L)
                .name("Test User")
                .username("test-user")
                .password("test-password")
                .profileImage("https://aws-bucket/my-profle")
                .linkToken(linkToken)
                .lockoutEndTime(LocalDateTime.now())
                .failedLoginAttempts(10)
                .build();
        User target = new User();

        //Act
        userMapper.updateUserProfile(target, source);

        //Assert
        assertNotNull(target);
        assertEquals(source.getUserId(), target.getUserId());
        assertEquals(source.getUsername(), target.getUsername());
        assertEquals(source.getLinkToken(), target.getLinkToken());
        assertEquals(source.getName(), target.getName());
        assertEquals(source.getProfileImage(), target.getProfileImage());
        assertEquals(source.getFailedLoginAttempts(), target.getFailedLoginAttempts());
        assertEquals(source.getLockoutEndTime(), target.getLockoutEndTime());
        assertEquals(source.getPassword(), target.getPassword());
    }
}
