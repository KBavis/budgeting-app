package com.bavis.budgetapp.services;


import com.bavis.budgetapp.dao.CategoryTypeRepository;
import com.bavis.budgetapp.dto.CategoryTypeDto;
import com.bavis.budgetapp.dto.UpdateCategoryTypeDto;
import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.exception.UserServiceException;
import com.bavis.budgetapp.mapper.CategoryTypeMapper;
import com.bavis.budgetapp.service.IncomeService;
import com.bavis.budgetapp.service.UserService;
import com.bavis.budgetapp.service.impl.CategoryTypeServiceImpl;
import com.bavis.budgetapp.util.GeneralUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles(profiles = "test")
public class CategoryTypeServiceTests {

    @Mock
    UserService userService;

    @Mock
    IncomeService incomeService;

    @Mock
    CategoryTypeMapper categoryTypeMapper;

    @Mock
    CategoryTypeRepository repository;

    @InjectMocks
    CategoryTypeServiceImpl categoryTypeService;

    private User user;

    private CategoryTypeDto categoryTypeDtoNeeds;

    private CategoryTypeDto categoryTypeDtoWants;
    private CategoryTypeDto categoryTypeDtoInvestments;

    private CategoryType categoryTypeNeeds;

    private CategoryType categoryTypeWants;
    private CategoryType categoryTypeInvestments;

    private ArgumentCaptor<CategoryType> argumentCaptor;

    @BeforeEach
    public void setup() {
        user = User.builder()
                .userId(10L)
                .build();

        argumentCaptor = ArgumentCaptor.forClass(CategoryType.class);

        //Arrange DTOs
       categoryTypeDtoNeeds = CategoryTypeDto.builder()
               .name("Needs")
               .budgetAllocationPercentage(.5)
               .build();

       categoryTypeDtoWants = CategoryTypeDto.builder()
               .name("Wants")
               .budgetAllocationPercentage(.3)
               .build();

       categoryTypeDtoInvestments = CategoryTypeDto.builder()
               .name("Investments")
               .budgetAllocationPercentage(.2)
               .build();

       //Arrange Entites
        categoryTypeNeeds = CategoryType.builder()
                .categoryTypeId(1L)
                .name("Needs")
                .budgetAllocationPercentage(.5)
                .build();

        categoryTypeWants = CategoryType.builder()
                .categoryTypeId(2L)
                .name("Wants")
                .budgetAllocationPercentage(.3)
                .build();

        categoryTypeInvestments = CategoryType.builder()
                .categoryTypeId(3L)
                .name("Investments")
                .budgetAllocationPercentage(.2)
                .build();

    }

    @Test
    void testRemoveCategory_NullCategory_NoUpdates() {
        categoryTypeService.removeCategory(null);
        Mockito.verify(repository, times(0)).save(any(CategoryType.class));
    }

    @Test
    void testRemoveCategory_SavedAmount_Updated() {
        //Arrange
        Category category1 = Category.builder()
                .categoryId(1L)
                .budgetAmount(100)
                .build();
        Category category2 = Category.builder()
                .categoryId(2L)
                .budgetAmount(200)
                .build();
        Category category3 = Category.builder()
                .categoryId(3L)
                .budgetAmount(300)
                .build();
        List<Category> categories = List.of(category1, category2, category3);
        CategoryType categoryType = CategoryType.builder()
                .categoryTypeId(10L)
                .categories(categories)
                .budgetAmount(600)
                .savedAmount(0)
                .build();
        category1.setCategoryType(categoryType);
        double expectedSavedAmount = categoryType.getBudgetAmount() - (category2.getBudgetAmount() + category3.getBudgetAmount());

        //Act
        categoryTypeService.removeCategory(category1);

        //Verify & Assert
        Mockito.verify(repository, times(1)).save(argumentCaptor.capture());
        CategoryType actualCategoryType = argumentCaptor.getValue();
        assertEquals(expectedSavedAmount, actualCategoryType.getSavedAmount());
    }

    @Test
    void testRemoveCategory_NullCategoryType_NoUpdates() {
        Category categoryWithNullCategoryType = Category.builder()
                .categoryType(null)
                .build();
        categoryTypeService.removeCategory(categoryWithNullCategoryType);
        Mockito.verify(repository, times(0)).save(any(CategoryType.class));
    }

    @Test
    void testRemoveCategory_UpdatesCategory_Success() {
        Category category = Category.builder()
                .categoryId(1L)
                .build();

        CategoryType categoryType = CategoryType.builder()
                .categoryTypeId(10L)
                .categories(List.of(category))
                .build();

        category.setCategoryType(categoryType);

        categoryTypeService.removeCategory(category);

        Mockito.verify(repository, times(1)).save(argumentCaptor.capture());
        CategoryType actualCategoryType = argumentCaptor.getValue();
        assertFalse(actualCategoryType.getCategories().contains(category));
    }

    @Test
    void testRemoveCategory_NullCategories_Success()  {
        CategoryType categoryType = CategoryType.builder()
                .categoryTypeId(10L)
                .categories(null)
                .build();

        Category category = Category.builder()
                .categoryId(1L)
                .categoryType(categoryType)
                .build();

        categoryTypeService.removeCategory(category);

        Mockito.verify(repository, times(1)).save(argumentCaptor.capture());
        CategoryType actualCategoryType = argumentCaptor.getValue();
        assertEquals(Collections.emptyList(), actualCategoryType.getCategories());
    }

    @Test
    void testReadByName_Successful() {
        //Arrange
        String categoryType = "Needs";

        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(repository.findByNameAndUserUserId(categoryType, user.getUserId())).thenReturn(categoryTypeNeeds);

        //Act
        CategoryType actualCategoryType = categoryTypeService.readByName(categoryType);

        //Assert
        assertEquals(categoryTypeNeeds, actualCategoryType);

        //Verify
        verify(userService, times(1)).getCurrentAuthUser();
        verify(repository, times(1)).findByNameAndUserUserId(categoryType,user.getUserId());
    }

    @Test
    void testReadByName_CorrectsCapitalization() {
        //Arrange
        String categoryType = "NEEDS";
        String expectedCategoryTypeName = GeneralUtil.toNormalCase(categoryType);

        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(repository.findByNameAndUserUserId(expectedCategoryTypeName, user.getUserId())).thenReturn(categoryTypeNeeds);

        //Act
        categoryTypeService.readByName(categoryType);


        //Verify
        verify(repository, times(1)).findByNameAndUserUserId(expectedCategoryTypeName,user.getUserId());
    }

    @Test
    void testReadByName_WithUser_Success() {
        //Arrange
        String categoryType = "Needs";

        //Mock
        when(repository.findByNameAndUserUserId(categoryType, user.getUserId())).thenReturn(categoryTypeNeeds);

        //Act
        CategoryType actualCategoryType = categoryTypeService.readByName(categoryType, user);

        //Assert
        assertEquals(categoryTypeNeeds, actualCategoryType);

        //Verify
        verify(repository, times(1)).findByNameAndUserUserId(categoryType,user.getUserId());
    }

    @Test
    void testReadByName_ReturnsNull() {
        //Arrange
        String categoryType = "Needs";

        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(repository.findByNameAndUserUserId(categoryType, user.getUserId())).thenReturn(null);

        //Act
        CategoryType actualCategoryType = categoryTypeService.readByName(categoryType);

        //Assert
        assertNull(actualCategoryType);

        //Verify
        verify(userService, times(1)).getCurrentAuthUser();
        verify(repository, times(1)).findByNameAndUserUserId(categoryType,user.getUserId());
    }

    @Test
    void testRead_Successful() {
        //Mock
        when(repository.findById(categoryTypeNeeds.getCategoryTypeId())).thenReturn(Optional.of(categoryTypeNeeds));

        //Act
        CategoryType actualCategoryType = categoryTypeService.read(categoryTypeNeeds.getCategoryTypeId());

        //Assert
        assertEquals(categoryTypeNeeds, actualCategoryType);
    }

    @Test
    void testRead_IdNotFound_ThrowsException() {
        //Mock
        when(repository.findById(categoryTypeNeeds.getCategoryTypeId())).thenReturn(Optional.empty());

        //Act
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            categoryTypeService.read(categoryTypeNeeds.getCategoryTypeId());
        });

        //Assert
        assertNotNull(runtimeException);
        assertEquals("Invalid category type id: " + categoryTypeNeeds.getCategoryTypeId(), runtimeException.getMessage());
    }

    @Test
    public void testCreateMany_Successful() {
        //Arrange
        double userTotalIncome = 15000.0;
        double needsExpectedAmount = userTotalIncome * categoryTypeNeeds.getBudgetAllocationPercentage();
        double wantsExpectedAmount = userTotalIncome * categoryTypeWants.getBudgetAllocationPercentage();
        double investmentsExpectedAmount = userTotalIncome * categoryTypeInvestments.getBudgetAllocationPercentage();
        List<CategoryTypeDto> categoryTypeDtos = new ArrayList<>(List.of(categoryTypeDtoInvestments, categoryTypeDtoNeeds, categoryTypeDtoWants));
        List<CategoryType> expectedCategoryTypes = new ArrayList<>(List.of(categoryTypeNeeds, categoryTypeWants, categoryTypeInvestments));

        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(incomeService.findUserTotalIncomeAmount(user.getUserId())).thenReturn(userTotalIncome);
        when(categoryTypeMapper.toEntity(categoryTypeDtoInvestments)).thenReturn(categoryTypeInvestments);
        when(categoryTypeMapper.toEntity(categoryTypeDtoNeeds)).thenReturn(categoryTypeNeeds);
        when(categoryTypeMapper.toEntity(categoryTypeDtoWants)).thenReturn(categoryTypeWants);
        when(repository.saveAllAndFlush(any())).thenReturn(expectedCategoryTypes);

        //Act
        List<CategoryType> categoryTypes = categoryTypeService.createMany(categoryTypeDtos);

        //Assert
        assertNotNull(categoryTypes);
        assertEquals(3, categoryTypes.size());
        for(CategoryType type: categoryTypes) {
            assertEquals(user.getUserId(), type.getUser().getUserId());
            if(type.getCategoryTypeId() == 1L) {
                assertEquals(needsExpectedAmount, type.getBudgetAmount());
            } else if(type.getCategoryTypeId() == 2L) {
                assertEquals(wantsExpectedAmount,type.getBudgetAmount());
            } else if(type.getCategoryTypeId() == 3L) {
                assertEquals(investmentsExpectedAmount, type.getBudgetAmount());
            } else {
                fail("Unexpected CategoryType ID Found!");
            }
        }
    }

    @Test
    void testReadAll_Successful() {
        //Arrange
        CategoryType categoryTypeOne = CategoryType.builder()
                .categoryTypeId(10L)
                .name("Needs")
                .budgetAllocationPercentage(.5)
                .categories(new ArrayList<>())
                .build();

        CategoryType categoryTypeTwo = CategoryType.builder()
                .categoryTypeId(11L)
                .name("Wants")
                .budgetAllocationPercentage(.2)
                .categories(new ArrayList<>())
                .build();

        CategoryType categoryTypeThree = CategoryType.builder()
                .categoryTypeId(12L)
                .name("Investments")
                .budgetAllocationPercentage(.3)
                .categories(new ArrayList<>())
                .build();

        List<CategoryType> expectedCategoryTypes = List.of(categoryTypeOne, categoryTypeTwo, categoryTypeThree);

        User currentAuthUSer = User.builder()
                .userId(10L)
                .username("auth-user")
                .build();

        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(currentAuthUSer);
        when(repository.findByUserUserId(user.getUserId())).thenReturn(expectedCategoryTypes);

        //Act
        List<CategoryType> actualCategoryTypes = categoryTypeService.readAll();

        //Assert
        assertNotNull(actualCategoryTypes);
        assertEquals(actualCategoryTypes.size(), 3);
        assertTrue(actualCategoryTypes.contains(categoryTypeOne));
        assertTrue(actualCategoryTypes.contains(categoryTypeTwo));
        assertTrue(actualCategoryTypes.contains(categoryTypeThree));
    }

    @Test
    void testReadAll_WithUser_Successful() {
        //Arrange
        CategoryType categoryTypeOne = CategoryType.builder()
                .categoryTypeId(10L)
                .name("Needs")
                .budgetAllocationPercentage(.5)
                .categories(new ArrayList<>())
                .build();

        CategoryType categoryTypeTwo = CategoryType.builder()
                .categoryTypeId(11L)
                .name("Wants")
                .budgetAllocationPercentage(.2)
                .categories(new ArrayList<>())
                .build();

        CategoryType categoryTypeThree = CategoryType.builder()
                .categoryTypeId(12L)
                .name("Investments")
                .budgetAllocationPercentage(.3)
                .categories(new ArrayList<>())
                .build();

        List<CategoryType> expectedCategoryTypes = List.of(categoryTypeOne, categoryTypeTwo, categoryTypeThree);

        User currentAuthUSer = User.builder()
                .userId(10L)
                .username("auth-user")
                .build();

        //Mock
        when(repository.findByUserUserId(user.getUserId())).thenReturn(expectedCategoryTypes);

        //Act
        List<CategoryType> actualCategoryTypes = categoryTypeService.readAll(currentAuthUSer);

        //Assert
        assertNotNull(actualCategoryTypes);
        assertEquals(actualCategoryTypes.size(), 3);
        assertTrue(actualCategoryTypes.contains(categoryTypeOne));
        assertTrue(actualCategoryTypes.contains(categoryTypeTwo));
        assertTrue(actualCategoryTypes.contains(categoryTypeThree));
    }

    @Test
    public void testCreateMany_UserServiceException_Failure() {
        //Arrange
        UserServiceException expectedException = new UserServiceException("Unable to find any Authenticated user");

        //Mock
        when(userService.getCurrentAuthUser()).thenThrow(expectedException);

        //Act & Assert
        UserServiceException actualException = assertThrows(UserServiceException.class, () -> {
            categoryTypeService.createMany(new ArrayList<>());
        });
        assertEquals(expectedException, actualException);
    }

    @Test
    public void testUpdateCategoryType_InvalidId_Failure() {
        //Arrange
        Long invalidCategoryTypeId = 10L;

        //Mock
        when(repository.findById(invalidCategoryTypeId)).thenReturn(Optional.empty());

        //Act & Assert
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            categoryTypeService.update(null, invalidCategoryTypeId);
        });
        assertNotNull(runtimeException);
        assertEquals("Invalid category type id: " + invalidCategoryTypeId, runtimeException.getMessage());
    }

    @Test
    public void testUpdateCategoryType_ValidId_Success() {
        //Arrange
        Long categoryTypeId = 10L;
        double budgetAllocationPerecentage = .5;
        double budgetAmount = 500;
        double savedAmount = 0;

        UpdateCategoryTypeDto updateCategoryTypeDto = UpdateCategoryTypeDto.builder()
                .savedAmount(savedAmount)
                .amountAllocated(budgetAmount)
                .savedAmount(savedAmount)
                .build();

        CategoryType expectedCategoryType = CategoryType.builder()
                .categoryTypeId(categoryTypeId)
                .categories(null)
                .budgetAmount(budgetAmount)
                .budgetAllocationPercentage(budgetAllocationPerecentage)
                .savedAmount(savedAmount)
                .build();

        //Mock
        when(repository.findById(categoryTypeId)).thenReturn(Optional.of(expectedCategoryType));
        when(repository.save(expectedCategoryType)).thenReturn(expectedCategoryType);

        //Act
        CategoryType actualCategoryType = categoryTypeService.update(updateCategoryTypeDto, categoryTypeId);

        //Assert
        assertEquals(expectedCategoryType, actualCategoryType);

        //Verify
        verify(repository, times(1)).findById(categoryTypeId);
        verify(repository, times(1)).save(expectedCategoryType);
    }
}
