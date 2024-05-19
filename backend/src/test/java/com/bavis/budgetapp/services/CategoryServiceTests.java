package com.bavis.budgetapp.services;

import com.bavis.budgetapp.dao.CategoryRepository;
import com.bavis.budgetapp.dto.BulkCategoryDto;
import com.bavis.budgetapp.dto.CategoryDto;
import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.mapper.CategoryMapper;
import com.bavis.budgetapp.service.UserService;
import com.bavis.budgetapp.service.impl.CategoryServiceImpl;
import com.bavis.budgetapp.service.impl.CategoryTypeServiceImpl;
import com.bavis.budgetapp.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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


    @BeforeEach
    public void setup() {

        category1 = Category.builder()
                .name("Restaurants")
                .budgetAmount(1000.0)
                .budgetAllocationPercentage(.60)
                .build();

        category2 = Category.builder()
                .name("Loans")
                .budgetAmount(400.0)
                .budgetAllocationPercentage(.20)
                .build();

        category3 = Category.builder()
                .name("Animal")
                .budgetAmount(400.0)
                .budgetAllocationPercentage(.20)
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
}
