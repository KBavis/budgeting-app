package com.bavis.budgetapp.services;

import com.bavis.budgetapp.dao.CategoryRepository;
import com.bavis.budgetapp.dto.AddCategoryDto;
import com.bavis.budgetapp.dto.BulkCategoryDto;
import com.bavis.budgetapp.dto.CategoryDto;
import com.bavis.budgetapp.dto.UpdateCategoryDto;
import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.mapper.CategoryMapper;
import com.bavis.budgetapp.service.impl.CategoryServiceImpl;
import com.bavis.budgetapp.service.impl.CategoryTypeServiceImpl;
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

    }

    @Test
    void testCreate_CategoryTypeUpdated() {

    }
    @Test
    void testCreate_ExceedBudget_ThrowsException() {

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
