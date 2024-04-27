package com.bavis.budgetapp.services;

import com.bavis.budgetapp.dao.UserRepository;
import com.bavis.budgetapp.exception.UserServiceException;
import com.bavis.budgetapp.mapper.UserMapper;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
public class UserServiceTests {

    @Mock
    UserRepository userRepository;

    @MockBean
    UserMapper userMapper;

    @InjectMocks
    UserServiceImpl userService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .name("Test User")
                .userId(10L)
                .password("test-password")
                .username("testuser")
                .build();

        userService = new UserServiceImpl(userRepository, userMapper);
    }

    @Test
    public void testCreate_Successful() {
        //Mock
        when(userRepository.save(any(User.class))).thenReturn(user);

        //Act
        User actualUser = userService.create(user);

        //Assert
        assertNotNull(actualUser);
        assertEquals("Test User", user.getName());
        assertEquals(10L, user.getUserId());
        assertEquals("test-password", user.getPassword());
        assertEquals("testuser", user.getUsername());


        verify(userRepository, times(1)).save(any(User.class));
    }


    @Test
    public void testReadById_Successful() {
        //Mock
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        //Act
        User acutalUser = userService.readById(user.getUserId());

        //Assert
        assertNotNull(acutalUser);
        assertEquals("Test User", user.getName());
        assertEquals(10L, user.getUserId());
        assertEquals("test-password", user.getPassword());
        assertEquals("testuser", user.getUsername());


        verify(userRepository, times(1)).findById(10L);
    }

    @Test
    public void testReadById_UserServiceException_Failure() {
        //Mock
        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        //Act & Assert
        UserServiceException ex = assertThrows(UserServiceException.class, () -> {
            userService.readById(user.getUserId());
        });
        assertNotNull(ex);
        assertEquals("UserServiceException: [Could not find user with the ID " + user.getUserId() + "]", ex.getMessage());

        verify(userRepository, times(1)).findById(10L);
    }

    @Test
    public void testReadByUsername_Successful() {
        //Mock
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        //Act
        User acutalUser = userService.readByUsername(user.getUsername());

        //Assert
        assertNotNull(acutalUser);
        assertEquals("Test User", user.getName());
        assertEquals(10L, user.getUserId());
        assertEquals("test-password", user.getPassword());
        assertEquals("testuser", user.getUsername());

        verify(userRepository, times(1)).findByUsername(user.getUsername());

    }

    @Test
    public void testReadByUsername_UserServiceException_Failure() {
        //Mock
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        //Act & Assert
        UserServiceException ex = assertThrows(UserServiceException.class, () -> {
            userService.readByUsername(user.getUsername());
        });
        assertNotNull(ex);
        assertEquals("UserServiceException: [Could not find user with the username " + user.getUsername() + "]", ex.getMessage());

        //Verify
        verify(userRepository, times(1)).findByUsername(user.getUsername());
    }

    @Test
    public void testUpdate_Successful() {
        //Arrange
        User updatedUser = User.builder()
                .name("Updated User")
                .userId(10L)
                .password("updated-password")
                .username("updateduser")
                .build();

        //Mock
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        doAnswer(i -> {
            User target = i.getArgument(0);
            User source = i.getArgument(1);
            target.setName(source.getName());
            target.setUsername(source.getUsername());
            target.setPassword(source.getPassword());
            return null;
        }).when(userMapper).updateUserProfile(any(User.class), any(User.class));
        when(userRepository.save(user)).thenReturn(updatedUser);

        //Act
        User actualUser = userService.update(user.getUserId(), updatedUser);

        //Assert
        assertNotNull(actualUser);
        assertEquals("Updated User", actualUser.getName());
        assertEquals(10L, actualUser.getUserId());
        assertEquals("updated-password", actualUser.getPassword());
        assertEquals("updateduser", actualUser.getUsername());

        verify(userRepository, times(1)).findById(user.getUserId());
        verify(userMapper, times(1)).updateUserProfile(any(User.class), any(User.class));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testUpdate_UserServiceException_Failure() {
        //Mock
        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        //Act & Assert
        UserServiceException ex = assertThrows(UserServiceException.class, () -> {
           userService.update(10L, null);
        });
        assertNotNull(ex);
        assertEquals("UserServiceException: [Could not find user with the ID " + user.getUserId() + "]", ex.getMessage());

       //Verify
       verify(userRepository, times(1)).findById(10L);
    }

    @Test
    public void testExistsByUsername_Successful() {
        //Mock
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        //Act
        boolean userExists = userService.existsByUsername(user.getUsername());

        //Assert
        assertTrue(userExists);
    }

    @Test
    @WithMockUser("testuser")
    public void testGetCurrentAuthUser_Successful() {
        //Mock
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        //Act
        User user = userService.getCurrentAuthUser();

        //Assert
        assertNotNull(user);
        assertEquals("Test User", user.getName());
        assertEquals(10L, user.getUserId());
        assertEquals("test-password", user.getPassword());
        assertEquals("testuser", user.getUsername());

        verify(userRepository, times(1)).findByUsername(user.getUsername());
    }

    @Test
    @WithMockUser("testuser")
    public void testGetCurrentAuthUser_UserServiceException_Failure() {
        //Mock
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        //Act & Assert
        UserServiceException ex = assertThrows(UserServiceException.class, () -> {
            userService.getCurrentAuthUser();
        });
        assertNotNull(ex);
        assertEquals("UserServiceException: [Could not find user with the username " + user.getUsername() + "]", ex.getMessage());

        //Verify
        verify(userRepository, times(1)).findByUsername(user.getUsername());
    }

    @Test
    public void testGetCurrentAuthUser_AuthenticationNull_Failure() {
        //Act
        User user = userService.getCurrentAuthUser();

        //Assert
        assertNull(user);
    }
}
