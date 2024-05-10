package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.dto.AuthRequestDto;
import com.bavis.budgetapp.service.UserService;
import com.bavis.budgetapp.service.impl.UserServiceImpl;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles(profiles = "test")
@ExtendWith(MockitoExtension.class)
public class AuthRequestDuplicateUsernameValidatorTests {

    @Mock
    UserServiceImpl userService;

    @InjectMocks
    AuthRequestDuplicateUsernameValidator validator;

    AuthRequestDto validAuthRequestDto;
    AuthRequestDto emptyUsernameAuthRequestDto;

    AuthRequestDto nullUsernameAuthRequestDto;
    @BeforeEach
    void setup() {
       validAuthRequestDto = AuthRequestDto.builder()
               .username("Test-User")
               .name("Test User")
               .passwordOne("password")
               .passwordTwo("password")
               .build();

       emptyUsernameAuthRequestDto = AuthRequestDto.builder()
               .username(null)
               .name("Test User")
               .passwordOne("password")
               .passwordTwo("password")
               .build();

       nullUsernameAuthRequestDto = AuthRequestDto.builder()
                .username(null)
                .name("Test User")
                .passwordOne("password")
                .passwordTwo("password")
                .build();
    }

    @Test
    public void testIsValid_ValidUsername_Successful() {
        //Mock
        when(userService.existsByUsername(validAuthRequestDto.getUsername())).thenReturn(false);
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        //Act
        boolean valid = validator.isValid(validAuthRequestDto, context);

        //Assert
        assertTrue(valid);
    }

    @Test
    public void testIsValid_UsernameTaken_Failure() {
        //Mock
        when(userService.existsByUsername(validAuthRequestDto.getUsername())).thenReturn(true);
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        //Act
        boolean valid = validator.isValid(validAuthRequestDto, context);

        //Assert
        assertFalse(valid);
    }


    @Test
    public void testIsValid_NullUsername_Failure() {
        //Mock
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        //Act
        boolean valid = validator.isValid(nullUsernameAuthRequestDto, context);

        //Assert
        assertFalse(valid);
    }
    @Test
    public void testIsValid_EmptyUsername_Failure() {
        //Mock
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        //Act
        boolean valid = validator.isValid(emptyUsernameAuthRequestDto, context);

        //Assert
        assertFalse(valid);
    }
}
