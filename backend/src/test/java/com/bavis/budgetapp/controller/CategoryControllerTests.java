package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.AddCategoryDto;
import com.bavis.budgetapp.dto.BulkCategoryDto;
import com.bavis.budgetapp.dto.CategoryDto;
import com.bavis.budgetapp.dto.EditCategoryDto;
import com.bavis.budgetapp.dto.UpdateCategoryDto;
import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.service.impl.CategoryServiceImpl;
import com.bavis.budgetapp.service.impl.CategoryTypeServiceImpl;
import com.bavis.budgetapp.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
@ActiveProfiles(profiles = "test")
public class CategoryControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryServiceImpl categoryService;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private CategoryTypeServiceImpl categoryTypeService;

    @InjectMocks
    private CategoryController categoryController;

    @Autowired
    private ObjectMapper objectMapper;

    private BulkCategoryDto succesfulBulkCategoryDto;

    private Category category1;
    private Category category2;
    private Category category3;

    private CategoryType categoryType;

    private List<Category> expectedCategoryList;

    private User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .userId(10L)
                .build();

        categoryType = CategoryType.builder()
                .categoryTypeId(10L)
                .budgetAmount(1800.0)
                .budgetAllocationPercentage(.50)
                .user(user)
                .build();

        category1 = Category.builder()
                .name("Restaurants")
                .categoryId(10L)
                .budgetAmount(1000.0)
                .budgetAllocationPercentage(.60)
                .categoryType(categoryType)
                .build();

        category2 = Category.builder()
                .name("Loans")
                .categoryId(11L)
                .budgetAmount(400.0)
                .budgetAllocationPercentage(.20)
                .categoryType(categoryType)
                .build();

        category3 = Category.builder()
                .name("Animal")
                .categoryId(12L)
                .budgetAmount(400.0)
                .budgetAllocationPercentage(.20)
                .categoryType(categoryType)
                .build();

        CategoryDto categoryDto1 = CategoryDto.builder()
                .categoryTypeId(10L)
                .name("Restaurants")
                .budgetAmount(1000.0)
                .budgetAllocationPercentage(.60)
                .build();


        CategoryDto categoryDto2 = CategoryDto.builder()
                .categoryTypeId(10L)
                .name("Loans")
                .budgetAmount(400.0)
                .budgetAllocationPercentage(.20)
                .build();


        CategoryDto categoryDto3 = CategoryDto.builder()
                .categoryTypeId(10L)
                .name("Animal")
                .budgetAmount(400.0)
                .budgetAllocationPercentage(.20)
                .build();

        succesfulBulkCategoryDto = BulkCategoryDto.builder()
                .categories(Arrays.asList(categoryDto1, categoryDto2, categoryDto3))
                .build();

        expectedCategoryList = Arrays.asList(category1, category2, category3);

    }

    @Test
    void testUpdate_Successful() throws Exception{
        //Arrange
        EditCategoryDto editCategoryDto = new EditCategoryDto();

        //Mock
        when(categoryService.update(editCategoryDto, 1L)).thenReturn(expectedCategoryList);

        //Act
        ResultActions resultActions = mockMvc.perform(put("/category/" + 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editCategoryDto)));

        //Assert
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].categoryId").value(category1.getCategoryId()))
                .andExpect(jsonPath("$[0].name").value(category1.getName()))
                .andExpect(jsonPath("$[0].budgetAmount").value(category1.getBudgetAmount()))
                .andExpect(jsonPath("$[0].budgetAllocationPercentage").value(category1.getBudgetAllocationPercentage()))
                .andExpect(jsonPath("$[1].categoryId").value(category2.getCategoryId()))
                .andExpect(jsonPath("$[1].name").value(category2.getName()))
                .andExpect(jsonPath("$[1].budgetAmount").value(category2.getBudgetAmount()))
                .andExpect(jsonPath("$[1].budgetAllocationPercentage").value(category2.getBudgetAllocationPercentage()))
                .andExpect(jsonPath("$[2].categoryId").value(category3.getCategoryId()))
                .andExpect(jsonPath("$[2].name").value(category3.getName()))
                .andExpect(jsonPath("$[2].budgetAmount").value(category3.getBudgetAmount()))
                .andExpect(jsonPath("$[2].budgetAllocationPercentage").value(category3.getBudgetAllocationPercentage()));
    }

    @Test
    void testUpdate_InvalidDto_ThrowsException() throws Exception{
        //Arrange
        EditCategoryDto editCategoryDto = new EditCategoryDto();

        //Mock
        when(categoryService.update(editCategoryDto, 1L)).thenThrow(new RuntimeException("Invalid EditCategoryDto; ensures updates are not null"));

        //Act
        ResultActions resultActions = mockMvc.perform(put("/category/" + 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editCategoryDto)));

        //Assert
        resultActions
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Invalid EditCategoryDto; ensures updates are not null"));
    }

    @Test
    @Order(1)
    public void testBulkCreate_Successful() throws Exception{
        //Mock
        when(categoryService.bulkCreate(succesfulBulkCategoryDto)).thenReturn(expectedCategoryList);

        //Mock UserService/CategoryTypeService to pass validations for CategoryType
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(categoryTypeService.read(any(long.class))).thenReturn(categoryType);

        //Act
        ResultActions resultActions = mockMvc.perform(post("/category/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(succesfulBulkCategoryDto)));

        // Assert
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].categoryId").value(category1.getCategoryId()))
                .andExpect(jsonPath("$[0].name").value(category1.getName()))
                .andExpect(jsonPath("$[0].budgetAmount").value(category1.getBudgetAmount()))
                .andExpect(jsonPath("$[0].budgetAllocationPercentage").value(category1.getBudgetAllocationPercentage()))
                .andExpect(jsonPath("$[1].categoryId").value(category2.getCategoryId()))
                .andExpect(jsonPath("$[1].name").value(category2.getName()))
                .andExpect(jsonPath("$[1].budgetAmount").value(category2.getBudgetAmount()))
                .andExpect(jsonPath("$[1].budgetAllocationPercentage").value(category2.getBudgetAllocationPercentage()))
                .andExpect(jsonPath("$[2].categoryId").value(category3.getCategoryId()))
                .andExpect(jsonPath("$[2].name").value(category3.getName()))
                .andExpect(jsonPath("$[2].budgetAmount").value(category3.getBudgetAmount()))
                .andExpect(jsonPath("$[2].budgetAllocationPercentage").value(category3.getBudgetAllocationPercentage()));

        //Verify
        verify(userService, times(3)).getCurrentAuthUser();
        verify(categoryTypeService, times(3)).read(any(long.class));
    }

    @Test
    void testCreate_Successful() throws Exception {
        //Arrange
        UpdateCategoryDto updateCategoryDto1 = UpdateCategoryDto.builder()
                .categoryId(category1.getCategoryId())
                .budgetAllocationPercentage(.4) //720
                .build();


        UpdateCategoryDto updateCategoryDto2 = UpdateCategoryDto.builder()
                .categoryId(category2.getCategoryId())
                .budgetAllocationPercentage(.2) //360
                .build();

        UpdateCategoryDto updateCategoryDto3 = UpdateCategoryDto.builder()
                .categoryId(category3.getCategoryId())
                .budgetAllocationPercentage(.1) //180
                .build();

        CategoryDto categoryToAdd = CategoryDto.builder()
                .categoryTypeId(10L)
                .name("New Category")
                .budgetAmount(540)
                .budgetAllocationPercentage(.3)
                .build();

        AddCategoryDto validDto = AddCategoryDto.builder()
                .updatedCategories(List.of(updateCategoryDto1, updateCategoryDto2, updateCategoryDto3))
                .addedCategory(categoryToAdd)
                .build();

        Category createdCategory = Category.builder()
                .categoryType(categoryType)
                .categoryId(4L)
                .name("New Category")
                .budgetAmount(540)
                .budgetAllocationPercentage(.3)
                .build();
        //Mock
        when(categoryService.create(validDto)).thenReturn(createdCategory);

        //Act & Assert
        mockMvc.perform(post("/category").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.budgetAllocationPercentage").value(createdCategory.getBudgetAllocationPercentage()))
                .andExpect(jsonPath("$.categoryType.categoryTypeId").value(categoryType.getCategoryTypeId()))
                .andExpect(jsonPath("$.budgetAmount").value(createdCategory.getBudgetAmount()))
                .andExpect(jsonPath("$.name").value(createdCategory.getName()))
                .andExpect(jsonPath("$.categoryId").value(createdCategory.getCategoryId()));

        //Verify
        verify(categoryService, times(1)).create(validDto);
    }

    @Test
    void testCreate_NullUpdateCategories_Failure() throws Exception {
        //Arrange
        AddCategoryDto invalidDto = AddCategoryDto.builder()
                .updatedCategories(null)
                .build();

        //Act
        ResultActions resultActions = mockMvc.perform(post("/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)));

        //Assert
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("The updated Categories or the created Category is null."));
    }

    @Test
    public void testBulkCreate_NullCategories_Failure() throws Exception{
        //Arrange
        BulkCategoryDto invalidBulkCategoryDto = BulkCategoryDto.builder()
                .categories(null)
                .build();

        // Act
        ResultActions resultActions = mockMvc.perform(post("/category/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBulkCategoryDto)));


        //Assert
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Must include at least one Category"));
    }

    @Test
    public void testBulkCreate_InvalidBudgetAmount_Failure() throws Exception{
        //Arrange
        CategoryDto categoryDto = CategoryDto.builder()
                .budgetAmount(-100.0)
                .categoryTypeId(10L)
                .name("Test Category")
                .budgetAllocationPercentage(.5)
                .build();
        List<CategoryDto> categories = Arrays.asList(categoryDto);
        BulkCategoryDto invalidBulkCategoryDto = BulkCategoryDto.builder()
                .categories(categories)
                .build();

        //Mock UserService/CategoryTypeService to pass validations for CategoryType
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(categoryTypeService.read(any(long.class))).thenReturn(categoryType);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/category/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidBulkCategoryDto)));


        //Assert
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Budget Amount must be a valid, positive numeric value."));

        //Verify
        verify(userService, times(1)).getCurrentAuthUser();
        verify(categoryTypeService, times(1)).read(any(long.class));
    }

    @Test
    public void testBulkCreate_InvalidName_Failure() throws Exception{
        //Arrange
        CategoryDto categoryDto = CategoryDto.builder()
                .budgetAmount(100.0)
                .categoryTypeId(10L)
                .name("")
                .budgetAllocationPercentage(.5)
                .build();
        List<CategoryDto> categories = Arrays.asList(categoryDto);
        BulkCategoryDto invalidBulkCategoryDto = BulkCategoryDto.builder()
                .categories(categories)
                .build();

        //Mock UserService/CategoryTypeService to pass validations for CategoryType
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(categoryTypeService.read(any(long.class))).thenReturn(categoryType);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/category/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidBulkCategoryDto)));


        //Assert
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Category name must be a valid combination of letters between 1 and 50 characters."));

        //Verify
        verify(userService, times(1)).getCurrentAuthUser();
        verify(categoryTypeService, times(1)).read(any(long.class));
    }

    @Test
    public void testBulkCreate_InvalidPercentBudget_Failure() throws Exception {
        //Arrange
        CategoryDto categoryDto = CategoryDto.builder()
                .budgetAmount(100.0)
                .categoryTypeId(10L)
                .name("Test Category")
                .budgetAllocationPercentage(-90.0)
                .build();
        List<CategoryDto> categories = Arrays.asList(categoryDto);
        BulkCategoryDto invalidBulkCategoryDto = BulkCategoryDto.builder()
                .categories(categories)
                .build();

        //Mock UserService/CategoryTypeService to pass validations for CategoryType
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(categoryTypeService.read(any(long.class))).thenReturn(categoryType);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/category/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidBulkCategoryDto)));


        //Assert
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Category must be a valid percentage between 1 - 100%"));


        //Verify
        verify(userService, times(1)).getCurrentAuthUser();
        verify(categoryTypeService, times(1)).read(any(long.class));
    }

    @Test
    public void testBulkCreate_InvalidCategoryType_Failure() throws Exception {
        //Arrange
        User badUser = User.builder()
                .userId(100L)
                .build();
        CategoryDto categoryDto = CategoryDto.builder()
                .budgetAmount(100.0)
                .categoryTypeId(10L)
                .name("Test Category")
                .budgetAllocationPercentage(.5)
                .build();
        List<CategoryDto> categories = Arrays.asList(categoryDto);
        BulkCategoryDto invalidBulkCategoryDto = BulkCategoryDto.builder()
                .categories(categories)
                .build();

        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(badUser); //return user that DOESN'T own category type
        when(categoryTypeService.read(any(long.class))).thenReturn(categoryType);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/category/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidBulkCategoryDto)));


        //Assert
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Category must have a valid association to a CategoryType"));
    }

    @Test
    void testReadAll_Successful() throws Exception{
        //Mock
        when(categoryService.readAll()).thenReturn(expectedCategoryList);

        //Act
        ResultActions resultActions = mockMvc.perform(get("/category"));

        //Assert

        // Assert
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].categoryId").value(category1.getCategoryId()))
                .andExpect(jsonPath("$[0].name").value(category1.getName()))
                .andExpect(jsonPath("$[0].budgetAmount").value(category1.getBudgetAmount()))
                .andExpect(jsonPath("$[0].budgetAllocationPercentage").value(category1.getBudgetAllocationPercentage()))
                .andExpect(jsonPath("$[1].categoryId").value(category2.getCategoryId()))
                .andExpect(jsonPath("$[1].name").value(category2.getName()))
                .andExpect(jsonPath("$[1].budgetAmount").value(category2.getBudgetAmount()))
                .andExpect(jsonPath("$[1].budgetAllocationPercentage").value(category2.getBudgetAllocationPercentage()))
                .andExpect(jsonPath("$[2].categoryId").value(category3.getCategoryId()))
                .andExpect(jsonPath("$[2].name").value(category3.getName()))
                .andExpect(jsonPath("$[2].budgetAmount").value(category3.getBudgetAmount()))
                .andExpect(jsonPath("$[2].budgetAllocationPercentage").value(category3.getBudgetAllocationPercentage()));

        //Verify
        verify(categoryService, times(1)).readAll();
    }
}
