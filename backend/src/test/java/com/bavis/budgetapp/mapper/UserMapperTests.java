package com.bavis.budgetapp.mapper;


import com.bavis.budgetapp.model.User;
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

    /**
     *
     @Mappings({
     @Mapping(target = "target.name", source = "source.name"),
     @Mapping(target = "target.username", source = "source.username"),
     @Mapping(target = "target.password", source = "source.password"),
     @Mapping(target = "target.profileImage", source = "source.profileImage"),
     @Mapping(target = "target.linkToken", source = "source.linkToken"),
     @Mapping(target = "target.failedLoginAttempts", source = "source.failedLoginAttempts"),
     @Mapping(target = "target.lockoutEndTime", source = "source.lockoutEndTime"),
     @Mapping(target = "categories", ignore = true),
     @Mapping(target = "accounts", ignore = true),
     @Mapping(target = "authorities", ignore = true)
     })
     */

    @Test
    public void testUpdateUserProfile_Successful() {
        //Arrange
        User source = User.builder()
                .userId(10L)
                .name("Test User")
                .username("test-user")
                .password("test-password")
                .profileImage("https://aws-bucket/my-profle")
                .linkToken("link-token")
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
