package com.bavis.budgetapp.services;

import com.bavis.budgetapp.dao.CategoryRepository;
import com.bavis.budgetapp.dto.AddCategoryDto;
import com.bavis.budgetapp.dto.BulkCategoryDto;
import com.bavis.budgetapp.dto.CategoryDto;
import com.bavis.budgetapp.dto.EditCategoryDto;
import com.bavis.budgetapp.dto.RenameCategoryDto;
import com.bavis.budgetapp.dto.UpdateCategoryDto;
import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.entity.Transaction;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.mapper.CategoryMapper;
import com.bavis.budgetapp.service.impl.CategoryServiceImpl;
import com.bavis.budgetapp.service.impl.CategoryTypeServiceImpl;
import com.bavis.budgetapp.service.impl.TransactionServiceImpl;
import com.bavis.budgetapp.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        category1 = Category.builder()
                .name("Restaurants")
                .budgetAmount(1000.0)
                .budgetAllocationPercentage(.60)
                .categoryId(1L)
                .build();

        category2 = Category.builder()
                .name("Loans")
                .budgetAmount(400.0)
                .budgetAllocationPercentage(.20)
                .categoryId(2L)
                .build();

        category3 = Category.builder()
                .name("Animal")
                .budgetAmount(400.0)
                .budgetAllocationPercentage(.20)
                .categoryId(3L)
                .build();

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

        List<CategoryDto> categories = Arrays.asList(categoryDto1, categoryDto2,categoryDto3);
        actualCategories = Arrays.asList(category1, category2, category3);

        bulkCategoryDto = BulkCategoryDto.builder()
                .categories(categories)
                .build();

        user = User.builder()
                .userId(10L)
                .build();

        needsCategoryType = CategoryType.builder()
                .categoryTypeId(10L)
                .name("Needs")
                .budgetAmount(1800.0)
                .budgetAllocationPercentage(.60)
                .build();

        updateCategoryDto1 = UpdateCategoryDto.builder()
                .categoryId(category1.getCategoryId())
                .budgetAllocationPercentage(.4) //720
                .build();


        updateCategoryDto2 = UpdateCategoryDto.builder()
                .categoryId(category2.getCategoryId())
                .budgetAllocationPercentage(.2) //360
                .build();

        updateCategoryDto3 = UpdateCategoryDto.builder()
                .categoryId(category3.getCategoryId())
                .budgetAllocationPercentage(.1) //180
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
        //Arrange
        List<Category> originalCategories = List.of(category1, category2, category3);
        CategoryType categoryType = CategoryType.builder()
                .categoryTypeId(10L)
                .categories(originalCategories)
                .budgetAmount(1800) //original categories use 100% of budget
                .savedAmount(0)
                .build();

        createdCategory = Category.builder()
                .categoryType(categoryType)
                .categoryId(4L)
                .name("New Category")
                .budgetAmount(540)
                .budgetAllocationPercentage(.3)
                .build();

        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(categoryTypeService.read(categoryToAdd.getCategoryTypeId())).thenReturn(categoryType);
        when(categoryMapper.toEntity(categoryToAdd)).thenReturn(createdCategory);
        when(categoryRepository.findByCategoryId(updateCategoryDto1.getCategoryId())).thenReturn(category1);
        when(categoryRepository.findByCategoryId(updateCategoryDto2.getCategoryId())).thenReturn(category2);
        when(categoryRepository.findByCategoryId(updateCategoryDto3.getCategoryId())).thenReturn(category3);

        //Act
        categoryService.create(addCategoryDto);

        //Verify
        verify(categoryRepository).saveAllAndFlush(categoryListCaptor.capture());

        //Assert
        List<Category> updatedCategories = categoryListCaptor.getValue();
        assertEquals(4, updatedCategories.size());
        assertCategoriesUpdatedProperly(updatedCategories);
    }

    @Test
    void testCreate_CategoryCreated() {
        //Arrange
        List<Category> originalCategories = List.of(category1, category2, category3);
        CategoryType categoryType = CategoryType.builder()
                .categoryTypeId(10L)
                .categories(originalCategories)
                .budgetAmount(1800) //original categories use 100% of budget
                .savedAmount(0)
                .build();

        createdCategory = Category.builder()
                .categoryType(categoryType)
                .categoryId(4L)
                .name("New Category")
                .budgetAmount(540)
                .budgetAllocationPercentage(.3)
                .build();

        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(categoryTypeService.read(categoryToAdd.getCategoryTypeId())).thenReturn(categoryType);
        when(categoryMapper.toEntity(categoryToAdd)).thenReturn(createdCategory);
        when(categoryRepository.findByCategoryId(updateCategoryDto1.getCategoryId())).thenReturn(category1);
        when(categoryRepository.findByCategoryId(updateCategoryDto2.getCategoryId())).thenReturn(category2);
        when(categoryRepository.findByCategoryId(updateCategoryDto3.getCategoryId())).thenReturn(category3);

        //Act
        Category newCategory = categoryService.create(addCategoryDto);

        //Assert
        assertEquals(createdCategory, newCategory);
    }

    @Test
    void testCreate_CategoryTypeUpdated() {
        //Arrange
        List<Category> originalCategories = List.of(category1, category2, category3);
        CategoryType categoryType = CategoryType.builder()
                .categoryTypeId(10L)
                .categories(originalCategories)
                .budgetAmount(1800) //original categories use 100% of budget
                .savedAmount(100)
                .build();

        createdCategory = Category.builder()
                .categoryType(categoryType)
                .categoryId(4L)
                .name("New Category")
                .budgetAmount(540)
                .budgetAllocationPercentage(.3)
                .build();

        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(categoryTypeService.read(categoryToAdd.getCategoryTypeId())).thenReturn(categoryType);
        when(categoryMapper.toEntity(categoryToAdd)).thenReturn(createdCategory);
        when(categoryRepository.findByCategoryId(updateCategoryDto1.getCategoryId())).thenReturn(category1);
        when(categoryRepository.findByCategoryId(updateCategoryDto2.getCategoryId())).thenReturn(category2);
        when(categoryRepository.findByCategoryId(updateCategoryDto3.getCategoryId())).thenReturn(category3);

        //Act
        categoryService.create(addCategoryDto);

        //Assert
        assertEquals(0, categoryType.getSavedAmount()); //ensure CategoryType updated to be 0
    }
    @Test
    void testCreate_ExceedBudget_ThrowsException() {
        //Arrange
        List<Category> originalCategories = List.of(category1, category2, category3);
        CategoryType categoryType = CategoryType.builder()
                .categoryTypeId(10L)
                .categories(originalCategories)
                .budgetAmount(1800) //original categories use 100% of budget
                .savedAmount(0)
                .build();

        createdCategory = Category.builder()
                .categoryType(categoryType)
                .categoryId(4L)
                .name("New Category")
                .budgetAmount(600) //this ensures the categoryType budget is exceeded by $60
                .budgetAllocationPercentage(.3)
                .build();

        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(categoryTypeService.read(categoryToAdd.getCategoryTypeId())).thenReturn(categoryType);
        when(categoryMapper.toEntity(categoryToAdd)).thenReturn(createdCategory);
        when(categoryRepository.findByCategoryId(updateCategoryDto1.getCategoryId())).thenReturn(category1);
        when(categoryRepository.findByCategoryId(updateCategoryDto2.getCategoryId())).thenReturn(category2);
        when(categoryRepository.findByCategoryId(updateCategoryDto3.getCategoryId())).thenReturn(category3);

        //Act
        assertThrows(RuntimeException.class, () -> categoryService.create(addCategoryDto));
    }

    @Test
    void testUpdateCategoryAllocations_NullDto_ThrowsException() {
        //Act & Assert
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            categoryService.updateCategoryAllocations(null);
        });
        assertEquals("Invalid EditCategoryDto; ensures updates are not null", runtimeException.getMessage());
    }

    @Test
    void testUpdateCategoryAllocations_ExpectedResult_Success() {
        //Arrange
        needsCategoryType.setCategories(List.of(category1, category2, category3));

        //Mock
        when(categoryTypeService.read(needsCategoryType.getCategoryTypeId())).thenReturn(needsCategoryType);
        when(categoryRepository.findByCategoryId(updateCategoryDto1.getCategoryId())).thenReturn(category1);
        when(categoryRepository.findByCategoryId(updateCategoryDto2.getCategoryId())).thenReturn(category2);
        when(categoryRepository.findByCategoryId(updateCategoryDto3.getCategoryId())).thenReturn(category3);

        //Act
        List<Category> categories = categoryService.updateCategoryAllocations(editCategoryDto);

        //Assert
        assertNotNull(categories);
        assertEquals(3, categories.size());
        assertCategoriesUpdatedProperly(categories);
    }

    @Test
    void testUpdateCategoryAllocations_CategoriesUpdated_Success() {
        //Arrange
        needsCategoryType.setCategories(List.of(category1, category2, category3));

        //Mock
        when(categoryTypeService.read(needsCategoryType.getCategoryTypeId())).thenReturn(needsCategoryType);
        when(categoryRepository.findByCategoryId(updateCategoryDto1.getCategoryId())).thenReturn(category1);
        when(categoryRepository.findByCategoryId(updateCategoryDto2.getCategoryId())).thenReturn(category2);
        when(categoryRepository.findByCategoryId(updateCategoryDto3.getCategoryId())).thenReturn(category3);

        //Act
        categoryService.updateCategoryAllocations(editCategoryDto);

        //Assert
        assertEquals(updateCategoryDto1.getBudgetAllocationPercentage(), category1.getBudgetAllocationPercentage());
        assertEquals(updateCategoryDto2.getBudgetAllocationPercentage(), category2.getBudgetAllocationPercentage());
        assertEquals(updateCategoryDto3.getBudgetAllocationPercentage(), category3.getBudgetAllocationPercentage());

        double needsCategoryTypeAllocationAmount = needsCategoryType.getBudgetAmount();
        assertEquals( needsCategoryTypeAllocationAmount * updateCategoryDto1.getBudgetAllocationPercentage(), category1.getBudgetAmount());
        assertEquals(needsCategoryTypeAllocationAmount * updateCategoryDto2.getBudgetAllocationPercentage(), category2.getBudgetAmount());
        assertEquals( needsCategoryTypeAllocationAmount * updateCategoryDto3.getBudgetAllocationPercentage(), category3.getBudgetAmount());
    }

    @Test
    void testUpdateCategoryAllocations_ExceedTotalBudget_ThrowsException() {
        //Arrange
        String expectedErrorMessage = "Category allocations, " + 7260.0 + ", exceed total budgeted amount for CategoryType " + needsCategoryType.getCategoryTypeId() + ": " + needsCategoryType.getBudgetAmount();
        Category existingNeedsCategory = Category.builder()
                .categoryId(200L)
                .budgetAmount(6000)
                .budgetAllocationPercentage(.8)
                .build();
        needsCategoryType.setCategories(List.of(category1, category2, category3, existingNeedsCategory));

        //Mock
        when(categoryTypeService.read(needsCategoryType.getCategoryTypeId())).thenReturn(needsCategoryType);
        when(categoryRepository.findByCategoryId(updateCategoryDto1.getCategoryId())).thenReturn(category1);
        when(categoryRepository.findByCategoryId(updateCategoryDto2.getCategoryId())).thenReturn(category2);
        when(categoryRepository.findByCategoryId(updateCategoryDto3.getCategoryId())).thenReturn(category3);

        //Act & Assert
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            categoryService.updateCategoryAllocations(editCategoryDto);
        });
        assertEquals(expectedErrorMessage, runtimeException.getMessage());
    }

    @Test
    void testDeleteCategory_Successful() {
        Category category = Category.builder()
                .categoryId(4L)
                .name("New Category")
                .budgetAmount(540)
                .budgetAllocationPercentage(.3)
                .build();

        Transaction transaction = new Transaction();
        transaction.setTransactionId("id");
        List<Transaction> transactions = List.of(transaction);

        doNothing().when(transactionService).removeAssignedCategory(any(String.class));
        doNothing().when(userService).removeCategory(category);
        doNothing().when(categoryRepository).deleteById(category.getCategoryId());
        when(transactionService.fetchCategoryTransactions(category.getCategoryId())).thenReturn(transactions);
        when(categoryRepository.findByCategoryId(category.getCategoryId())).thenReturn(category);

        categoryService.delete(category.getCategoryId());

        verify(transactionService, times(1)).fetchCategoryTransactions(category.getCategoryId());
        verify(transactionService, times(1)).removeAssignedCategory(transaction.getTransactionId());
        verify(categoryTypeService, times(1)).removeCategory(category);
        verify(categoryRepository, times(1)).deleteById(category.getCategoryId());
        verify(categoryRepository, times(1)).findByCategoryId(category.getCategoryId());
    }

    @Test
    void testDeleteCategory_CategoryNotFound_ThrowsException() {
        long categoryId = 1L;
        String expectedErrorMsg = "Invalid category id: " + categoryId;
        when(categoryRepository.findByCategoryId(any(Long.class))).thenThrow(new RuntimeException(expectedErrorMsg));
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> categoryService.delete(categoryId));
        assertEquals(expectedErrorMsg, runtimeException.getMessage());
    }



    @Test
    void testUpdateCategoryAllocations_CategoryTypeSavedAmount_Success() {
        //Arrange
        needsCategoryType.setCategories(List.of(category1, category2, category3));

        //Mock
        when(categoryTypeService.read(needsCategoryType.getCategoryTypeId())).thenReturn(needsCategoryType);
        when(categoryRepository.findByCategoryId(updateCategoryDto1.getCategoryId())).thenReturn(category1);
        when(categoryRepository.findByCategoryId(updateCategoryDto2.getCategoryId())).thenReturn(category2);
        when(categoryRepository.findByCategoryId(updateCategoryDto3.getCategoryId())).thenReturn(category3);

        //Act
        categoryService.updateCategoryAllocations(editCategoryDto);

        //Assert
        double totalBudgetAmount =
                updateCategoryDto1.getBudgetAllocationPercentage() * needsCategoryType.getBudgetAmount() +
                        updateCategoryDto2.getBudgetAllocationPercentage() * needsCategoryType.getBudgetAmount() +
                            updateCategoryDto3.getBudgetAllocationPercentage() * needsCategoryType.getBudgetAmount();

        double expectedSavedAmount = needsCategoryType.getBudgetAmount() - totalBudgetAmount;
        assertEquals(expectedSavedAmount, needsCategoryType.getSavedAmount());
    }

    @Test
    void testRenameCategory_Successful() {
        //Arrange
        RenameCategoryDto renameCategoryDto = RenameCategoryDto.builder()
                .categoryName("Valid Name")
                .categoryId(category1.getCategoryId())
                .build();

        //Mock
        when(categoryRepository.findByCategoryId(renameCategoryDto.getCategoryId())).thenReturn(category1);
        when(categoryRepository.saveAndFlush(any(Category.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //Act
        Category updatedCategory = categoryService.renameCategory(renameCategoryDto);

        //Assert
        assertEquals(renameCategoryDto.getCategoryName(), updatedCategory.getName());

        //Verify
        verify(categoryRepository, times(1)).saveAndFlush(any(Category.class));
    }

    @Test
    void testRenameCategory_InvalidCategoryId_Fail() {
        //Arrange
        RenameCategoryDto renameCategoryDto = RenameCategoryDto.builder()
                .categoryName("Valid Name")
                .categoryId(11L)
                .build();

        //Mock
        when(categoryRepository.findByCategoryId(renameCategoryDto.getCategoryId())).thenThrow(new RuntimeException("Invalid category id: " + renameCategoryDto.getCategoryId()));

        //Act & Assert
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            categoryService.renameCategory(renameCategoryDto);
        });
        assertEquals("Invalid category id: " + renameCategoryDto.getCategoryId(), runtimeException.getMessage());
    }


    @Test
    void testReadAll_Successful() {
        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(categoryRepository.findByUserUserId(user.getUserId())).thenReturn(actualCategories);

        //Act
        List<Category> returnedCategories = categoryService.readAll();

        //Assert
        assertNotNull(returnedCategories);
        assertEquals(actualCategories, returnedCategories);
    }

    /**
     * Validate CategoryService ability to successfully bulk create Categories
     */
    @Test
    public void testBulkCreate_Successful() {
        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(categoryTypeService.read(10L)).thenReturn(needsCategoryType);
        when(categoryMapper.toEntity(categoryDto1)).thenReturn(category1);
        when(categoryMapper.toEntity(categoryDto2)).thenReturn(category2);
        when(categoryMapper.toEntity(categoryDto3)).thenReturn(category3);
        when(categoryRepository.saveAllAndFlush(ArgumentMatchers.anyList())).thenReturn(actualCategories);

        //Act
        List<Category> createdCategories = categoryService.bulkCreate(bulkCategoryDto);

        //Assert
        assertNotNull(createdCategories);
        assertEquals(3, createdCategories.size());
        for(Category category : createdCategories){
            if(category.getName().equals(category1.getName())){
               assertEquals(category1.getName(), category.getName());
               assertEquals(category1.getBudgetAmount(), category.getBudgetAmount());
               assertEquals(category1.getBudgetAllocationPercentage(), category.getBudgetAllocationPercentage());
            } else if(category.getName().equals(category2.getName())){
                assertEquals(category2.getName(), category.getName());
                assertEquals(category2.getBudgetAllocationPercentage(), category.getBudgetAllocationPercentage());
                assertEquals(category2.getBudgetAmount(), category.getBudgetAmount());
            } else if(category.getName().equals(category3.getName())){
                assertEquals(category3.getName(), category.getName());
                assertEquals(category3.getBudgetAmount(), category.getBudgetAmount());
                assertEquals(category3.getBudgetAllocationPercentage(), category.getBudgetAllocationPercentage());
            } else {
                fail("Unrecognized Category was created!");
            }
        }

        //Ensure CategoryType Saved Amount Updated as Expected
        double totalCategoryAllocations = createdCategories.stream().mapToDouble(Category::getBudgetAmount).sum();
        double expectedSavedAmount = needsCategoryType.getBudgetAmount() - totalCategoryAllocations;
        assertEquals(expectedSavedAmount, needsCategoryType.getSavedAmount());
    }


    /**
     * Utility function to ensure Categories updated properly
     *
     * @param categories
     *          - Categories to validateupdates for
     */
    private void assertCategoriesUpdatedProperly(List<Category> categories) {
        for(Category category : categories) {
            Long currentCategoryId = category.getCategoryId();

            if(currentCategoryId.equals(category1.getCategoryId())) {
                assertEquals(720, category.getBudgetAmount());
                assertEquals(.4, category.getBudgetAllocationPercentage());
            } else if (currentCategoryId.equals(category2.getCategoryId())) {
                assertEquals(360, category.getBudgetAmount());
                assertEquals(.2, category.getBudgetAllocationPercentage());
            } else if (currentCategoryId.equals(category3.getCategoryId())) {
                assertEquals(180, category.getBudgetAmount());
                assertEquals(.1, category.getBudgetAllocationPercentage());
            } else if (currentCategoryId.equals(createdCategory.getCategoryId())) {
                assertEquals(.3, category.getBudgetAllocationPercentage());
                assertEquals(540, category.getBudgetAmount());
            } else {
                fail("Unrecognized Category updated");
            }

        }
    }
}
