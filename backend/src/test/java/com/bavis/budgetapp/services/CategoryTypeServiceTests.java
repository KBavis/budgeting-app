package com.bavis.budgetapp.services;

import com.bavis.budgetapp.dao.CategoryTypeRepository;
import com.bavis.budgetapp.dto.request.CategoryTypeDto;
import com.bavis.budgetapp.dto.request.UpdateCategoryTypeDto;
import com.bavis.budgetapp.dto.response.CategoryTypeResponseDto;
import com.bavis.budgetapp.constants.TemporalConstants;
import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.entity.CategoryTypeVt;
import com.bavis.budgetapp.entity.CategoryVt;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.exception.UserServiceException;
import com.bavis.budgetapp.mapper.CategoryTypeMapper;
import com.bavis.budgetapp.service.EffectivityService;
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
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    @Spy
    EffectivityService effectivityService = new EffectivityService();

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

        // Arrange DTOs
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

        // Arrange Entities with VT
        categoryTypeNeeds = CategoryType.builder()
                .categoryTypeId(1L)
                .user(user)
                .build();
        CategoryTypeVt vtNeeds = CategoryTypeVt.builder()
                .categoryType(categoryTypeNeeds)
                .name("Needs")
                .budgetAllocationPercentage(.5)
                .budgetAmount(1000.0)
                .savedAmount(0.0)
                .startDate(TemporalConstants.BEGINNING_OF_TIME)
                .endDate(TemporalConstants.END_OF_TIME)
                .build();
        categoryTypeNeeds.getValidTimes().add(vtNeeds);

        categoryTypeWants = CategoryType.builder()
                .categoryTypeId(2L)
                .user(user)
                .build();
        CategoryTypeVt vtWants = CategoryTypeVt.builder()
                .categoryType(categoryTypeWants)
                .name("Wants")
                .budgetAllocationPercentage(.3)
                .budgetAmount(600.0)
                .savedAmount(0.0)
                .startDate(TemporalConstants.BEGINNING_OF_TIME)
                .endDate(TemporalConstants.END_OF_TIME)
                .build();
        categoryTypeWants.getValidTimes().add(vtWants);

        categoryTypeInvestments = CategoryType.builder()
                .categoryTypeId(3L)
                .user(user)
                .build();
        CategoryTypeVt vtInvestments = CategoryTypeVt.builder()
                .categoryType(categoryTypeInvestments)
                .name("Investments")
                .budgetAllocationPercentage(.2)
                .budgetAmount(400.0)
                .savedAmount(0.0)
                .startDate(TemporalConstants.BEGINNING_OF_TIME)
                .endDate(TemporalConstants.END_OF_TIME)
                .build();
        categoryTypeInvestments.getValidTimes().add(vtInvestments);
    }

    @Test
    void testRemoveCategory_NullCategory_NoUpdates() {
        categoryTypeService.removeCategory(null);
        Mockito.verify(repository, times(0)).save(any(CategoryType.class));
    }

    @Test
    void testRemoveCategory_SavedAmount_Updated() {
        // Arrange
        Category category1 = Category.builder().categoryId(1L).build();
        CategoryVt catVt1 = CategoryVt.builder()
                .category(category1)
                .budgetAmount(100)
                .startDate(TemporalConstants.BEGINNING_OF_TIME)
                .endDate(TemporalConstants.END_OF_TIME)
                .build();
        category1.getValidTimes().add(catVt1);

        Category category2 = Category.builder().categoryId(2L).build();
        CategoryVt catVt2 = CategoryVt.builder()
                .category(category2)
                .budgetAmount(200)
                .startDate(TemporalConstants.BEGINNING_OF_TIME)
                .endDate(TemporalConstants.END_OF_TIME)
                .build();
        category2.getValidTimes().add(catVt2);

        Category category3 = Category.builder().categoryId(3L).build();
        CategoryVt catVt3 = CategoryVt.builder()
                .category(category3)
                .budgetAmount(300)
                .startDate(TemporalConstants.BEGINNING_OF_TIME)
                .endDate(TemporalConstants.END_OF_TIME)
                .build();
        category3.getValidTimes().add(catVt3);

        List<Category> categories = List.of(category1, category2, category3);
        CategoryType categoryType = CategoryType.builder()
                .categoryTypeId(10L)
                .categories(categories)
                .build();
        CategoryTypeVt ctVt = CategoryTypeVt.builder()
                .categoryType(categoryType)
                .name("Test")
                .budgetAllocationPercentage(.5)
                .budgetAmount(600)
                .savedAmount(0)
                .startDate(TemporalConstants.BEGINNING_OF_TIME)
                .endDate(TemporalConstants.END_OF_TIME)
                .build();
        categoryType.getValidTimes().add(ctVt);

        catVt1.setCategoryType(categoryType);
        catVt2.setCategoryType(categoryType);
        catVt3.setCategoryType(categoryType);

        double expectedSavedAmount = 600 - (200 + 300);

        // Act
        categoryTypeService.removeCategory(category1);

        // Verify & Assert
        Mockito.verify(repository, times(1)).save(argumentCaptor.capture());
        CategoryType actualCategoryType = argumentCaptor.getValue();
        CategoryTypeVt activeSavedVt = effectivityService.getActiveVt(actualCategoryType.getValidTimes(), LocalDate.now());
        assertEquals(expectedSavedAmount, activeSavedVt.getSavedAmount());
    }

    @Test
    void testRemoveCategory_NullCategoryType_NoUpdates() {
        Category categoryWithNullCategoryType = Category.builder().build();
        CategoryVt catVt = CategoryVt.builder()
                .category(categoryWithNullCategoryType)
                .categoryType(null)
                .startDate(TemporalConstants.BEGINNING_OF_TIME)
                .endDate(TemporalConstants.END_OF_TIME)
                .build();
        categoryWithNullCategoryType.getValidTimes().add(catVt);

        categoryTypeService.removeCategory(categoryWithNullCategoryType);
        Mockito.verify(repository, times(0)).save(any(CategoryType.class));
    }

    @Test
    void testRemoveCategory_UpdatesCategory_Success() {
        Category category = Category.builder().categoryId(1L).build();
        CategoryType categoryType = CategoryType.builder()
                .categoryTypeId(10L)
                .categories(new ArrayList<>(List.of(category)))
                .build();
        CategoryTypeVt ctVt = CategoryTypeVt.builder()
                .categoryType(categoryType)
                .name("Needs")
                .budgetAllocationPercentage(.5)
                .budgetAmount(500)
                .savedAmount(0)
                .startDate(TemporalConstants.BEGINNING_OF_TIME)
                .endDate(TemporalConstants.END_OF_TIME)
                .build();
        categoryType.getValidTimes().add(ctVt);

        CategoryVt catVt = CategoryVt.builder()
                .category(category)
                .categoryType(categoryType)
                .budgetAmount(100)
                .startDate(TemporalConstants.BEGINNING_OF_TIME)
                .endDate(TemporalConstants.END_OF_TIME)
                .build();
        category.getValidTimes().add(catVt);

        categoryTypeService.removeCategory(category);

        Mockito.verify(repository, times(1)).save(argumentCaptor.capture());
        CategoryType actualCategoryType = argumentCaptor.getValue();
        assertFalse(actualCategoryType.getCategories().contains(category));
    }

    @Test
    void testRemoveCategory_NullCategories_Success() {
        CategoryType categoryType = CategoryType.builder()
                .categoryTypeId(10L)
                .categories(null)
                .build();
        CategoryTypeVt ctVt = CategoryTypeVt.builder()
                .categoryType(categoryType)
                .name("Needs")
                .budgetAllocationPercentage(.5)
                .budgetAmount(500)
                .savedAmount(0)
                .startDate(TemporalConstants.BEGINNING_OF_TIME)
                .endDate(TemporalConstants.END_OF_TIME)
                .build();
        categoryType.getValidTimes().add(ctVt);

        Category category = Category.builder().categoryId(1L).build();
        CategoryVt catVt = CategoryVt.builder()
                .category(category)
                .categoryType(categoryType)
                .budgetAmount(100)
                .startDate(TemporalConstants.BEGINNING_OF_TIME)
                .endDate(TemporalConstants.END_OF_TIME)
                .build();
        category.getValidTimes().add(catVt);

        categoryTypeService.removeCategory(category);

        Mockito.verify(repository, times(1)).save(argumentCaptor.capture());
        CategoryType actualCategoryType = argumentCaptor.getValue();
        assertEquals(Collections.emptyList(), actualCategoryType.getCategories());
    }

    @Test
    void testReadByName_Successful() {
        // Arrange
        String categoryType = "Needs";

        // Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(repository.findByNameAndUserUserIdAndAsOf(eq(categoryType), eq(user.getUserId()), any())).thenReturn(Optional.of(categoryTypeNeeds));

        // Act
        CategoryType actualCategoryType = categoryTypeService.findEntityByName(categoryType, null);

        // Assert
        assertEquals(categoryTypeNeeds, actualCategoryType);

        // Verify
        verify(userService, times(1)).getCurrentAuthUser();
        verify(repository, times(1)).findByNameAndUserUserIdAndAsOf(eq(categoryType), eq(user.getUserId()), any());
    }

    @Test
    void testReadByName_CorrectsCapitalization() {
        // Arrange
        String categoryType = "NEEDS";
        String expectedCategoryTypeName = GeneralUtil.toNormalCase(categoryType);

        // Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(repository.findByNameAndUserUserIdAndAsOf(eq(expectedCategoryTypeName), eq(user.getUserId()), any())).thenReturn(Optional.of(categoryTypeNeeds));

        // Act
        categoryTypeService.findEntityByName(categoryType, null);

        // Verify
        verify(repository, times(1)).findByNameAndUserUserIdAndAsOf(eq(expectedCategoryTypeName), eq(user.getUserId()), any());
    }

    @Test
    void testReadByName_WithUser_Success() {
        // Arrange
        String categoryType = "Needs";

        // Mock
        when(repository.findByNameAndUserUserIdAndAsOf(eq(categoryType), eq(user.getUserId()), any())).thenReturn(Optional.of(categoryTypeNeeds));

        // Act
        CategoryType actualCategoryType = categoryTypeService.findEntityByName(categoryType, user, null);

        // Assert
        assertEquals(categoryTypeNeeds, actualCategoryType);

        // Verify
        verify(repository, times(1)).findByNameAndUserUserIdAndAsOf(eq(categoryType), eq(user.getUserId()), any());
    }

    @Test
    void testReadByName_ReturnsNull() {
        // Arrange
        String categoryType = "Needs";

        // Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(repository.findByNameAndUserUserIdAndAsOf(eq(categoryType), eq(user.getUserId()), any())).thenReturn(Optional.empty());

        // Act
        CategoryType actualCategoryType = categoryTypeService.findEntityByName(categoryType, null);

        // Assert
        assertNull(actualCategoryType);

        // Verify
        verify(userService, times(1)).getCurrentAuthUser();
        verify(repository, times(1)).findByNameAndUserUserIdAndAsOf(eq(categoryType), eq(user.getUserId()), any());
    }

    @Test
    void testFindEntity_Successful() {
        // Mock
        when(repository.findByCategoryTypeIdAndAsOf(eq(categoryTypeNeeds.getCategoryTypeId()), any())).thenReturn(Optional.of(categoryTypeNeeds));

        // Act
        CategoryType actualCategoryType = categoryTypeService.findEntity(categoryTypeNeeds.getCategoryTypeId(), null);

        // Assert
        assertEquals(categoryTypeNeeds, actualCategoryType);
    }

    @Test
    void testFindEntity_IdNotFound_ThrowsException() {
        // Mock
        when(repository.findByCategoryTypeIdAndAsOf(eq(categoryTypeNeeds.getCategoryTypeId()), any())).thenReturn(Optional.empty());

        // Act
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            categoryTypeService.findEntity(categoryTypeNeeds.getCategoryTypeId(), null);
        });

        // Assert
        assertNotNull(runtimeException);
        assertEquals("Invalid category type id: " + categoryTypeNeeds.getCategoryTypeId(), runtimeException.getMessage());
    }

    @Test
    public void testCreateMany_Successful() {
        // Arrange
        double userTotalIncome = 15000.0;
        List<CategoryTypeDto> categoryTypeDtos = new ArrayList<>(List.of(categoryTypeDtoInvestments, categoryTypeDtoNeeds, categoryTypeDtoWants));
        List<CategoryType> expectedCategoryTypes = new ArrayList<>(List.of(categoryTypeNeeds, categoryTypeWants, categoryTypeInvestments));

        // Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(incomeService.findUserTotalIncomeAmount(eq(user.getUserId()), any())).thenReturn(userTotalIncome);
        when(repository.saveAllAndFlush(any())).thenReturn(expectedCategoryTypes);
        when(categoryTypeMapper.toResponseDto(any(), any())).thenAnswer(inv -> {
            CategoryType ct = inv.getArgument(0);
            return CategoryTypeResponseDto.builder().categoryTypeId(ct.getCategoryTypeId()).build();
        });

        // Act
        List<CategoryTypeResponseDto> categoryTypes = categoryTypeService.createMany(categoryTypeDtos);

        // Assert
        assertNotNull(categoryTypes);
        assertEquals(3, categoryTypes.size());
    }

    @Test
    void testReadAll_Successful() {
        // Arrange
        CategoryType categoryTypeOne = CategoryType.builder().categoryTypeId(10L).categories(new ArrayList<>()).build();
        CategoryType categoryTypeTwo = CategoryType.builder().categoryTypeId(11L).categories(new ArrayList<>()).build();
        CategoryType categoryTypeThree = CategoryType.builder().categoryTypeId(12L).categories(new ArrayList<>()).build();

        List<CategoryType> expectedCategoryTypes = List.of(categoryTypeOne, categoryTypeTwo, categoryTypeThree);

        User currentAuthUser = User.builder().userId(10L).username("auth-user").build();

        // Mock
        when(userService.getCurrentAuthUser()).thenReturn(currentAuthUser);
        when(repository.findByUserUserIdAndAsOf(eq(currentAuthUser.getUserId()), any())).thenReturn(expectedCategoryTypes);

        // Act
        List<CategoryType> actualCategoryTypes = categoryTypeService.findAllEntities(null);

        // Assert
        assertNotNull(actualCategoryTypes);
        assertEquals(actualCategoryTypes.size(), 3);
        assertTrue(actualCategoryTypes.contains(categoryTypeOne));
        assertTrue(actualCategoryTypes.contains(categoryTypeTwo));
        assertTrue(actualCategoryTypes.contains(categoryTypeThree));
    }

    @Test
    void testReadAll_WithUser_Successful() {
        // Arrange
        CategoryType categoryTypeOne = CategoryType.builder().categoryTypeId(10L).categories(new ArrayList<>()).build();
        CategoryType categoryTypeTwo = CategoryType.builder().categoryTypeId(11L).categories(new ArrayList<>()).build();
        CategoryType categoryTypeThree = CategoryType.builder().categoryTypeId(12L).categories(new ArrayList<>()).build();

        List<CategoryType> expectedCategoryTypes = List.of(categoryTypeOne, categoryTypeTwo, categoryTypeThree);

        User currentAuthUser = User.builder().userId(10L).username("auth-user").build();

        // Mock
        when(repository.findByUserUserIdAndAsOf(eq(user.getUserId()), any())).thenReturn(expectedCategoryTypes);

        // Act
        List<CategoryType> actualCategoryTypes = categoryTypeService.findAllEntities(user, null);

        // Assert
        assertNotNull(actualCategoryTypes);
        assertEquals(actualCategoryTypes.size(), 3);
        assertTrue(actualCategoryTypes.contains(categoryTypeOne));
        assertTrue(actualCategoryTypes.contains(categoryTypeTwo));
        assertTrue(actualCategoryTypes.contains(categoryTypeThree));
    }

    @Test
    public void testCreateMany_UserServiceException_Failure() {
        // Arrange
        UserServiceException expectedException = new UserServiceException("Unable to find any Authenticated user");

        // Mock
        when(userService.getCurrentAuthUser()).thenThrow(expectedException);

        // Act & Assert
        UserServiceException actualException = assertThrows(UserServiceException.class, () -> {
            categoryTypeService.createMany(new ArrayList<>());
        });
        assertEquals(expectedException, actualException);
    }

    @Test
    public void testUpdateCategoryType_InvalidId_Failure() {
        // Arrange
        Long invalidCategoryTypeId = 10L;

        // Mock
        when(repository.findByCategoryTypeIdAndAsOf(eq(invalidCategoryTypeId), any())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            categoryTypeService.update(null, invalidCategoryTypeId);
        });
        assertNotNull(runtimeException);
        assertEquals("Invalid category type id: " + invalidCategoryTypeId, runtimeException.getMessage());
    }

    @Test
    public void testUpdateCategoryType_ValidId_Success() {
        // Arrange
        Long categoryTypeId = 10L;
        double budgetAllocationPercentage = .5;
        double budgetAmount = 500;
        double savedAmount = 0;

        UpdateCategoryTypeDto updateCategoryTypeDto = UpdateCategoryTypeDto.builder()
                .savedAmount(savedAmount)
                .amountAllocated(budgetAmount)
                .build();

        CategoryType expectedCategoryType = CategoryType.builder()
                .categoryTypeId(categoryTypeId)
                .categories(null)
                .build();
        CategoryTypeVt ctVt = CategoryTypeVt.builder()
                .categoryType(expectedCategoryType)
                .name("Test")
                .budgetAllocationPercentage(budgetAllocationPercentage)
                .budgetAmount(budgetAmount)
                .savedAmount(savedAmount)
                .startDate(TemporalConstants.BEGINNING_OF_TIME)
                .endDate(TemporalConstants.END_OF_TIME)
                .build();
        expectedCategoryType.getValidTimes().add(ctVt);

        CategoryTypeResponseDto expectedResponse = CategoryTypeResponseDto.builder()
                .categoryTypeId(categoryTypeId)
                .name("Test")
                .build();

        // Mock
        when(repository.findByCategoryTypeIdAndAsOf(eq(categoryTypeId), any())).thenReturn(Optional.of(expectedCategoryType));
        when(repository.save(expectedCategoryType)).thenReturn(expectedCategoryType);
        when(categoryTypeMapper.toResponseDto(any(), any())).thenReturn(expectedResponse);

        // Act
        CategoryTypeResponseDto actualCategoryType = categoryTypeService.update(updateCategoryTypeDto, categoryTypeId);

        // Assert
        assertEquals(expectedResponse, actualCategoryType);

        // Verify
        verify(repository, times(1)).findByCategoryTypeIdAndAsOf(eq(categoryTypeId), any());
        verify(repository, times(1)).save(expectedCategoryType);
    }
}
