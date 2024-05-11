package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.dto.CategoryDto;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.service.CategoryTypeService;
import com.bavis.budgetapp.service.UserService;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "test")
public class CategoryDtoCategoryTypeValidatorTests {

    @InjectMocks
    CategoryDtoCategoryTypeValidator validator;

    @Mock
    UserService userService;

    @Mock
    CategoryTypeService categoryTypeService;

    @Mock
    ConstraintValidatorContext context;

    CategoryDto validCategoryDto;

    CategoryDto invalidCategoryDto;

    CategoryType categoryType;

    User user;

    @BeforeEach
    void setup(){
       validCategoryDto = CategoryDto.builder()
               .categoryTypeId(10L)
               .build();

       invalidCategoryDto = new CategoryDto();

       user = User.builder()
               .userId(10L)
               .username("username")
               .build();

        categoryType = CategoryType.builder()
                .categoryTypeId(10L)
                .categories(new ArrayList<>())
                .budgetAmount(1000.0)
                .budgetAllocationPercentage(.7)
                .user(user)
                .build();
    }

    @Test
    void testIsValid_ValidCategoryType_Successful() {
       when(categoryTypeService.read(validCategoryDto.getCategoryTypeId())).thenReturn(categoryType);
       when(userService.getCurrentAuthUser()).thenReturn(user);

       assertTrue(validator.isValid(validCategoryDto, context));
    }

    @Test
    void testIsValid_InvalidCategoryTypeId_Failure() {
        invalidCategoryDto.setCategoryTypeId(11L);
        when(categoryTypeService.read(invalidCategoryDto.getCategoryTypeId())).thenThrow(new RuntimeException("Invalid category type id: " + invalidCategoryDto.getCategoryTypeId()));


        assertFalse(validator.isValid(invalidCategoryDto, context));
    }

    @Test
    void testIsValid_InvalidUser_Failure() {
        User invalidUser = User.builder()
                .userId(3L)
                .username("test")
                .build();
        invalidCategoryDto.setCategoryTypeId(11L);
        when(categoryTypeService.read(invalidCategoryDto.getCategoryTypeId())).thenReturn(categoryType);
        when(userService.getCurrentAuthUser()).thenReturn(invalidUser);

        assertFalse(validator.isValid(invalidCategoryDto, context));
    }
}
