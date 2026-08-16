package com.bavis.budgetapp.services;

import com.bavis.budgetapp.dao.CategoryRepository;
import com.bavis.budgetapp.dto.request.AddCategoryDto;
import com.bavis.budgetapp.dto.request.BulkCategoryDto;
import com.bavis.budgetapp.dto.request.CategoryDto;
import com.bavis.budgetapp.dto.request.EditCategoryDto;
import com.bavis.budgetapp.dto.request.RenameCategoryDto;
import com.bavis.budgetapp.dto.request.UpdateCategoryDto;
import com.bavis.budgetapp.dto.request.UpdateCategoryTypeDto;
import com.bavis.budgetapp.dto.response.CategoryResponseDto;
import com.bavis.budgetapp.constants.TemporalConstants;
import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.entity.CategoryTypeVt;
import com.bavis.budgetapp.entity.CategoryVt;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.mapper.CategoryMapper;
import com.bavis.budgetapp.service.EffectivityService;
import com.bavis.budgetapp.service.impl.CategoryServiceImpl;
import com.bavis.budgetapp.service.impl.CategoryTypeServiceImpl;
import com.bavis.budgetapp.service.impl.TransactionServiceImpl;
import com.bavis.budgetapp.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles(profiles = "test")
public class CategoryServiceTests {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryTypeServiceImpl categoryTypeService;

    @Mock
    UserServiceImpl userService;

    @Mock
    CategoryMapper categoryMapper;

    @Mock
    TransactionServiceImpl transactionService;

    @Spy
    private EffectivityService effectivityService = new EffectivityService();

    @Captor
    private ArgumentCaptor<List<Category>> categoryListCaptor;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private BulkCategoryDto bulkCategoryDto;
    private User user;
    private CategoryType needsCategoryType;

    private CategoryDto categoryDto1;
    private CategoryDto categoryDto2;
    private CategoryDto categoryDto3;

    private Category category1;
    private Category category2;
    private Category category3;

    private List<Category> actualCategories;

    private AddCategoryDto addCategoryDto;
    private UpdateCategoryDto updateCategoryDto1;
    private UpdateCategoryDto updateCategoryDto2;
    private UpdateCategoryDto updateCategoryDto3;

    private CategoryDto categoryToAdd;
    private Category createdCategory;
    private EditCategoryDto editCategoryDto;

    @BeforeEach
    public void setup() {
        user = User.builder()
                .userId(10L)
                .build();

        category1 = Category.builder()
                .categoryId(1L)
                .user(user)
                .build();
        CategoryVt catVt1 = CategoryVt.builder()
                .category(category1)
                .name("Restaurants")
                .budgetAmount(1000.0)
                .budgetAllocationPercentage(.60)
                .startDate(TemporalConstants.BEGINNING_OF_TIME)
                .endDate(TemporalConstants.END_OF_TIME)
                .build();
        category1.getValidTimes().add(catVt1);

        category2 = Category.builder()
                .categoryId(2L)
                .user(user)
                .build();
        CategoryVt catVt2 = CategoryVt.builder()
                .category(category2)
                .name("Loans")
                .budgetAmount(400.0)
                .budgetAllocationPercentage(.20)
                .startDate(TemporalConstants.BEGINNING_OF_TIME)
                .endDate(TemporalConstants.END_OF_TIME)
                .build();
        category2.getValidTimes().add(catVt2);

        category3 = Category.builder()
                .categoryId(3L)
                .user(user)
                .build();
        CategoryVt catVt3 = CategoryVt.builder()
                .category(category3)
                .name("Animal")
                .budgetAmount(400.0)
                .budgetAllocationPercentage(.20)
                .startDate(TemporalConstants.BEGINNING_OF_TIME)
                .endDate(TemporalConstants.END_OF_TIME)
                .build();
        category3.getValidTimes().add(catVt3);

        actualCategories = Arrays.asList(category1, category2, category3);

        needsCategoryType = CategoryType.builder()
                .categoryTypeId(10L)
                .categories(actualCategories)
                .build();
        CategoryTypeVt ctVt = CategoryTypeVt.builder()
                .categoryType(needsCategoryType)
                .name("Needs")
                .budgetAmount(1800.0)
                .budgetAllocationPercentage(.60)
                .savedAmount(0.0)
                .startDate(TemporalConstants.BEGINNING_OF_TIME)
                .endDate(TemporalConstants.END_OF_TIME)
                .build();
        needsCategoryType.getValidTimes().add(ctVt);

        catVt1.setCategoryType(needsCategoryType);
        catVt2.setCategoryType(needsCategoryType);
        catVt3.setCategoryType(needsCategoryType);

        categoryDto1 = CategoryDto.builder()
                .categoryTypeId(10L)
                .name("Restaurants")
                .budgetAmount(1000.0)
                .budgetAllocationPercentage(.60)
                .build();

        categoryDto2 = CategoryDto.builder()
                .categoryTypeId(10L)
                .name("Loans")
                .budgetAmount(400.0)
                .budgetAllocationPercentage(.20)
                .build();

        categoryDto3 = CategoryDto.builder()
                .categoryTypeId(10L)
                .name("Animal")
                .budgetAmount(400.0)
                .budgetAllocationPercentage(.20)
                .build();

        List<CategoryDto> categories = Arrays.asList(categoryDto1, categoryDto2, categoryDto3);

        bulkCategoryDto = BulkCategoryDto.builder()
                .categories(categories)
                .build();

        updateCategoryDto1 = UpdateCategoryDto.builder()
                .categoryId(category1.getCategoryId())
                .budgetAllocationPercentage(.4)
                .build();

        updateCategoryDto2 = UpdateCategoryDto.builder()
                .categoryId(category2.getCategoryId())
                .budgetAllocationPercentage(.2)
                .build();

        updateCategoryDto3 = UpdateCategoryDto.builder()
                .categoryId(category3.getCategoryId())
                .budgetAllocationPercentage(.1)
                .build();

        categoryToAdd = CategoryDto.builder()
                .categoryTypeId(10L)
                .name("New Category")
                .budgetAmount(540)
                .budgetAllocationPercentage(.3)
                .build();

        addCategoryDto = AddCategoryDto.builder()
                .addedCategory(categoryToAdd)
                .updatedCategories(List.of(updateCategoryDto1, updateCategoryDto2, updateCategoryDto3))
                .build();

        editCategoryDto = EditCategoryDto.builder()
                .updatedCategories(List.of(updateCategoryDto1, updateCategoryDto2, updateCategoryDto3))
                .categoryTypeId(needsCategoryType.getCategoryTypeId())
                .build();
    }

    @Test
    void testCreate_CategoriesUpdated() {
        // Arrange
        List<Category> originalCategories = List.of(category1, category2, category3);
        CategoryType categoryType = CategoryType.builder()
                .categoryTypeId(10L)
                .categories(originalCategories)
                .build();
        CategoryTypeVt ctVt = CategoryTypeVt.builder()
                .categoryType(categoryType)
                .name("Needs")
                .budgetAmount(1800.0)
                .savedAmount(0.0)
                .startDate(TemporalConstants.BEGINNING_OF_TIME)
                .endDate(TemporalConstants.END_OF_TIME)
                .build();
        categoryType.getValidTimes().add(ctVt);

        createdCategory = Category.builder()
                .categoryId(4L)
                .build();
        CategoryVt createdVt = CategoryVt.builder()
                .category(createdCategory)
                .categoryType(categoryType)
                .name("New Category")
                .budgetAmount(540.0)
                .budgetAllocationPercentage(.3)
                .startDate(TemporalConstants.BEGINNING_OF_TIME)
                .endDate(TemporalConstants.END_OF_TIME)
                .build();
        createdCategory.getValidTimes().add(createdVt);

        // Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(categoryTypeService.findEntity(categoryToAdd.getCategoryTypeId(), null)).thenReturn(categoryType);
        when(categoryRepository.findByCategoryIdAndAsOf(eq(updateCategoryDto1.getCategoryId()), any())).thenReturn(Optional.of(category1));
        when(categoryRepository.findByCategoryIdAndAsOf(eq(updateCategoryDto2.getCategoryId()), any())).thenReturn(Optional.of(category2));
        when(categoryRepository.findByCategoryIdAndAsOf(eq(updateCategoryDto3.getCategoryId()), any())).thenReturn(Optional.of(category3));

        // Act
        categoryService.create(addCategoryDto);

        // Verify
        verify(categoryRepository).saveAllAndFlush(categoryListCaptor.capture());

        // Assert
        List<Category> updatedCategories = categoryListCaptor.getValue();
        assertEquals(4, updatedCategories.size());
        assertCategoriesUpdatedProperly(updatedCategories);
    }

    @Test
    void testCreate_ExceedBudget_ThrowsException() {
        // Arrange
        List<Category> originalCategories = List.of(category1, category2, category3);
        CategoryType categoryType = CategoryType.builder()
                .categoryTypeId(10L)
                .categories(originalCategories)
                .build();
        CategoryTypeVt ctVt = CategoryTypeVt.builder()
                .categoryType(categoryType)
                .name("Needs")
                .budgetAmount(1800.0)
                .savedAmount(0.0)
                .startDate(TemporalConstants.BEGINNING_OF_TIME)
                .endDate(TemporalConstants.END_OF_TIME)
                .build();
        categoryType.getValidTimes().add(ctVt);

        CategoryDto invalidCategoryToAdd = CategoryDto.builder()
                .categoryTypeId(10L)
                .name("New Category")
                .budgetAmount(600)
                .budgetAllocationPercentage(.4) // Exceeds budget
                .build();
        AddCategoryDto invalidAddCategoryDto = AddCategoryDto.builder()
                .addedCategory(invalidCategoryToAdd)
                .updatedCategories(List.of(updateCategoryDto1, updateCategoryDto2, updateCategoryDto3))
                .build();

        // Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(categoryTypeService.findEntity(invalidCategoryToAdd.getCategoryTypeId(), null)).thenReturn(categoryType);
        when(categoryRepository.findByCategoryIdAndAsOf(eq(updateCategoryDto1.getCategoryId()), any())).thenReturn(Optional.of(category1));
        when(categoryRepository.findByCategoryIdAndAsOf(eq(updateCategoryDto2.getCategoryId()), any())).thenReturn(Optional.of(category2));
        when(categoryRepository.findByCategoryIdAndAsOf(eq(updateCategoryDto3.getCategoryId()), any())).thenReturn(Optional.of(category3));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> categoryService.create(invalidAddCategoryDto));
    }

    @Test
    void testUpdateCategoryAllocations_NullDto_ThrowsException() {
        // Act & Assert
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            categoryService.updateCategoryAllocations(null);
        });
        assertEquals("Invalid EditCategoryDto; ensures updates are not null", runtimeException.getMessage());
    }

    @Test
    void testUpdateCategoryAllocations_ExpectedResult_Success() {
        // Arrange
        needsCategoryType.setCategories(List.of(category1, category2, category3));

        // Mock
        when(categoryTypeService.findEntity(needsCategoryType.getCategoryTypeId(), null)).thenReturn(needsCategoryType);
        when(categoryRepository.findByCategoryIdAndAsOf(eq(updateCategoryDto1.getCategoryId()), any())).thenReturn(Optional.of(category1));
        when(categoryRepository.findByCategoryIdAndAsOf(eq(updateCategoryDto2.getCategoryId()), any())).thenReturn(Optional.of(category2));
        when(categoryRepository.findByCategoryIdAndAsOf(eq(updateCategoryDto3.getCategoryId()), any())).thenReturn(Optional.of(category3));

        // Act
        List<CategoryResponseDto> categories = categoryService.updateCategoryAllocations(editCategoryDto);

        // Assert
        assertNotNull(categories);
        assertEquals(3, categories.size());
    }

    @Test
    void testDelete_ValidCategory_Success() {
        Category categoryToDelete = Category.builder()
                .categoryId(1L)
                .build();
        CategoryVt catVt = CategoryVt.builder()
                .category(categoryToDelete)
                .name("Restaurants")
                .startDate(TemporalConstants.BEGINNING_OF_TIME)
                .endDate(TemporalConstants.END_OF_TIME)
                .build();
        categoryToDelete.getValidTimes().add(catVt);

        when(categoryRepository.findByCategoryIdAndAsOf(eq(1L), any())).thenReturn(Optional.of(categoryToDelete));
        when(categoryRepository.save(any(Category.class))).thenReturn(categoryToDelete);

        categoryService.delete(1L);

        verify(categoryRepository, times(1)).save(categoryToDelete);
        verify(categoryRepository, times(1)).findByCategoryIdAndAsOf(eq(1L), any());
    }

    @Test
    void testDeleteCategory_CategoryNotFound_ThrowsException() {
        long categoryId = 1L;
        String expectedErrorMsg = "Invalid category id: " + categoryId;
        when(categoryRepository.findByCategoryIdAndAsOf(eq(categoryId), any())).thenReturn(Optional.empty());
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> categoryService.delete(categoryId));
        assertEquals(expectedErrorMsg, runtimeException.getMessage());
    }

    @Test
    void testRenameCategory_Successful() {
        // Arrange
        RenameCategoryDto renameCategoryDto = RenameCategoryDto.builder()
                .categoryName("Valid Name")
                .categoryId(category1.getCategoryId())
                .build();
        CategoryResponseDto expectedResponse = CategoryResponseDto.builder()
                .categoryId(category1.getCategoryId())
                .name("Valid Name")
                .build();

        // Mock
        when(categoryRepository.findByCategoryIdAndAsOf(eq(renameCategoryDto.getCategoryId()), any())).thenReturn(Optional.of(category1));
        when(categoryRepository.saveAndFlush(any(Category.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(categoryMapper.toResponseDto(any(), any())).thenReturn(expectedResponse);

        // Act
        CategoryResponseDto updatedCategory = categoryService.renameCategory(renameCategoryDto);

        // Assert
        assertEquals(renameCategoryDto.getCategoryName(), updatedCategory.getName());

        // Verify
        verify(categoryRepository, times(1)).saveAndFlush(any(Category.class));
    }

    @Test
    void testRenameCategory_InvalidCategoryId_Fail() {
        // Arrange
        RenameCategoryDto renameCategoryDto = RenameCategoryDto.builder()
                .categoryName("Valid Name")
                .categoryId(11L)
                .build();

        // Mock
        when(categoryRepository.findByCategoryIdAndAsOf(eq(renameCategoryDto.getCategoryId()), any())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            categoryService.renameCategory(renameCategoryDto);
        });
        assertEquals("Invalid category id: " + renameCategoryDto.getCategoryId(), runtimeException.getMessage());
    }

    @Test
    void testReadAll_Successful() {
        // Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(categoryRepository.findByUserUserIdAndAsOf(eq(user.getUserId()), any())).thenReturn(actualCategories);

        // Act
        List<Category> returnedCategories = categoryService.findAllEntities(null);

        // Assert
        assertNotNull(returnedCategories);
        assertEquals(actualCategories, returnedCategories);
    }

    @Test
    public void testBulkCreate_Successful() {
        // Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(categoryTypeService.findEntity(eq(10L), any())).thenReturn(needsCategoryType);
        when(categoryRepository.saveAllAndFlush(any())).thenReturn(actualCategories);

        // Act
        List<CategoryResponseDto> createdCategories = categoryService.bulkCreate(bulkCategoryDto);

        // Assert
        assertNotNull(createdCategories);
        assertEquals(3, createdCategories.size());
    }

    private void assertCategoriesUpdatedProperly(List<Category> categories) {
        for (Category category : categories) {
            Long currentCategoryId = category.getCategoryId();
            CategoryVt active = effectivityService.getActiveVt(category.getValidTimes(), LocalDate.now());

            if (currentCategoryId.equals(category1.getCategoryId())) {
                assertEquals(720, active.getBudgetAmount());
                assertEquals(.4, active.getBudgetAllocationPercentage());
            } else if (currentCategoryId.equals(category2.getCategoryId())) {
                assertEquals(360, active.getBudgetAmount());
                assertEquals(.2, active.getBudgetAllocationPercentage());
            } else if (currentCategoryId.equals(category3.getCategoryId())) {
                assertEquals(180, active.getBudgetAmount());
                assertEquals(.1, active.getBudgetAllocationPercentage());
            } else if (currentCategoryId.equals(4L)) {
                assertEquals(.3, active.getBudgetAllocationPercentage());
                assertEquals(540, active.getBudgetAmount());
            } else {
                fail("Unrecognized Category updated");
            }
        }
    }
}
