package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.CategoryTypeDto;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.service.CategoryTypeService;
import com.bavis.budgetapp.service.impl.CategoryTypeServiceImpl;
import com.bavis.budgetapp.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TODO: Add tests for other functionality based on usage
 */
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
@ActiveProfiles(profiles = "test")
public class CategoryTypeControllerTests {

    @Autowired
    MockMvc mockMvc;
    
    @MockBean
    private CategoryTypeServiceImpl categoryTypeService;

    @MockBean
    private UserServiceImpl userService;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testReadAll_Successful() throws Exception{
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

        //Mock
        when(categoryTypeService.readAll()).thenReturn(expectedCategoryTypes);

        //Act & Assert
        ResultActions resultActions = mockMvc.perform(get("/category/type"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].categoryTypeId").value(categoryTypeOne.getCategoryTypeId()))
                .andExpect(jsonPath("$[0].name").value(categoryTypeOne.getName()))
                .andExpect(jsonPath("$[0].budgetAllocationPercentage").value(categoryTypeOne.getBudgetAllocationPercentage()))
                .andExpect(jsonPath("$[1].categoryTypeId").value(categoryTypeTwo.getCategoryTypeId()))
                .andExpect(jsonPath("$[1].name").value(categoryTypeTwo.getName()))
                .andExpect(jsonPath("$[1].budgetAllocationPercentage").value(categoryTypeTwo.getBudgetAllocationPercentage()))
                .andExpect(jsonPath("$[2].categoryTypeId").value(categoryTypeThree.getCategoryTypeId()))
                .andExpect(jsonPath("$[2].name").value(categoryTypeThree.getName()))
                .andExpect(jsonPath("$[2].budgetAllocationPercentage").value(categoryTypeThree.getBudgetAllocationPercentage()));

        //Verify
        verify(categoryTypeService ,times(1)).readAll();
    }

    @Test
    public void testBulkCreateCategoryType_ValidRequest_Successful() throws Exception {
        //Arrange
        CategoryTypeDto categoryTypeDto1 = CategoryTypeDto.builder()
                .name("Category Type One")
                .budgetAllocationPercentage(.5)
                .build();

        CategoryTypeDto categoryTypeDto2 = CategoryTypeDto.builder()
                .name("Category Type Two")
                .budgetAllocationPercentage(.25)
                .build();

        CategoryTypeDto categoryTypeDto3 = CategoryTypeDto.builder()
                .name("Category Type Three")
                .budgetAllocationPercentage(.25)
                .build();
        List<CategoryTypeDto> categoryTypeDtos = List.of(categoryTypeDto1, categoryTypeDto2, categoryTypeDto3);


        CategoryType categoryType1 = CategoryType.builder()
                .name("Category Type One")
                .budgetAllocationPercentage(.5)
                .budgetAmount(5000)
                .categories(new ArrayList<>())
                .build();

        CategoryType categoryType2 = CategoryType.builder()
                .name("Category Type Two")
                .budgetAllocationPercentage(.25)
                .budgetAmount(2500)
                .categories(new ArrayList<>())
                .build();

        CategoryType categoryType3 = CategoryType.builder()
                .name("Category Type Three")
                .budgetAllocationPercentage(.25)
                .budgetAmount(2500)
                .categories(new ArrayList<>())
                .build();

        List<CategoryType> categoryTypes = List.of(categoryType1, categoryType2, categoryType3);


       //Mock
       when(categoryTypeService.createMany(categoryTypeDtos)).thenReturn(categoryTypes);

       //Act
        ResultActions resultActions = mockMvc.perform(post("/category/type/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryTypeDtos)));

        //Assert
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].categoryTypeId").value(categoryType1.getCategoryTypeId()))
                .andExpect(jsonPath("$[0].name").value(categoryType1.getName()))
                .andExpect(jsonPath("$[0].budgetAmount").value(categoryType1.getBudgetAmount()))
                .andExpect(jsonPath("$[0].budgetAllocationPercentage").value(categoryType1.getBudgetAllocationPercentage()))
                .andExpect(jsonPath("$[0].categories").value(categoryType1.getCategories()))
                .andExpect(jsonPath("$[1].categoryTypeId").value(categoryType2.getCategoryTypeId()))
                .andExpect(jsonPath("$[1].name").value(categoryType2.getName()))
                .andExpect(jsonPath("$[1].budgetAmount").value(categoryType2.getBudgetAmount()))
                .andExpect(jsonPath("$[1].budgetAllocationPercentage").value(categoryType2.getBudgetAllocationPercentage()))
                .andExpect(jsonPath("$[1].categories").value(categoryType2.getCategories()))
                .andExpect(jsonPath("$[2].categoryTypeId").value(categoryType3.getCategoryTypeId()))
                .andExpect(jsonPath("$[2].name").value(categoryType3.getName()))
                .andExpect(jsonPath("$[2].budgetAmount").value(categoryType3.getBudgetAmount()))
                .andExpect(jsonPath("$[2].budgetAllocationPercentage").value(categoryType3.getBudgetAllocationPercentage()))
                .andExpect(jsonPath("$[2].categories").value(categoryType3.getCategories()));
    }
    
}
