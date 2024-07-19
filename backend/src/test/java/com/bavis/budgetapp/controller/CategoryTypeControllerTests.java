package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.CategoryTypeDto;
import com.bavis.budgetapp.dto.UpdateCategoryTypeDto;
import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.entity.CategoryType;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    void testRead_Successful() throws Exception {
        //Arrange
        Category categoryOne = Category.builder()
                .categoryId(1L)
                .name("My Test Category")
                .build();
        CategoryType categoryType = CategoryType.builder()
                .categoryTypeId(10L)
                .categories(List.of(categoryOne))
                .budgetAmount(1000.0)
                .budgetAllocationPercentage(.5)
                .savedAmount(100.0)
                .build();

        //Mock
        when(categoryTypeService.read(10L)).thenReturn(categoryType);

        //Act
        ResultActions resultActions = mockMvc.perform(get("/category/type/" + categoryType.getCategoryTypeId()));

        //Assert
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.categoryTypeId").value(categoryType.getCategoryTypeId()))
                .andExpect(jsonPath("$.budgetAmount").value(categoryType.getBudgetAmount()))
                .andExpect(jsonPath("$.budgetAllocationPercentage").value(categoryType.getBudgetAllocationPercentage()))
                .andExpect(jsonPath("$.savedAmount").value(categoryType.getSavedAmount()));
    }

    @Test
    void testRead_IdNotFound_Failure() throws Exception {
        //Arrange
        long categoryTypeId = 1L;
        RuntimeException runtimeException = new RuntimeException("Invalid category type id: " + categoryTypeId);

        //Mock
        when(categoryTypeService.read(1L)).thenThrow(runtimeException);

        //Act
        ResultActions resultActions = mockMvc.perform(get("/category/type/" + categoryTypeId));

        //Assert
        resultActions
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Invalid category type id: " + categoryTypeId));
    }

    @Test
    void testReadAll_Successful() throws Exception {
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
        verify(categoryTypeService, times(1)).readAll();
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

    @Test
    void testUpdateCategoryType_ValidRequest_Success() throws Exception {
        //Arrange
        Long categoryTypeId = 10L;
        UpdateCategoryTypeDto updateCategoryTypeDto = UpdateCategoryTypeDto.builder()
                .amountAllocated(100)
                .budgetAllocationPercentage(.5)
                .savedAmount(15)
                .build();

        CategoryType expectedCategoryType = CategoryType.builder()
                .categoryTypeId(categoryTypeId)
                .categories(null)
                .savedAmount(15)
                .budgetAllocationPercentage(.5)
                .budgetAmount(100)
                .build();

        //Mock
        when(categoryTypeService.update(updateCategoryTypeDto, categoryTypeId)).thenReturn(expectedCategoryType);

        //Act
        ResultActions resultActions = mockMvc.perform(put("/category/type/" + categoryTypeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateCategoryTypeDto)));

        //Assert
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.categoryTypeId").value(categoryTypeId))
                .andExpect(jsonPath("$.savedAmount").value(updateCategoryTypeDto.getSavedAmount()))
                .andExpect(jsonPath("$.budgetAllocationPercentage").value(updateCategoryTypeDto.getBudgetAllocationPercentage()))
                .andExpect(jsonPath("$.budgetAmount").value(updateCategoryTypeDto.getAmountAllocated()));

        //Verify
        verify(categoryTypeService, times(1)).update(updateCategoryTypeDto, categoryTypeId);
    }

    @Test
    void testUpdateCategoryType_NotFound_Failure() throws  Exception {
        //Arrange
        Long invalidCategoryTypeId = 10L;
        UpdateCategoryTypeDto updateCategoryTypeDto = UpdateCategoryTypeDto.builder()
                .amountAllocated(100)
                .budgetAllocationPercentage(.5)
                .savedAmount(15)
                .build();

        //Mock
        when(categoryTypeService.update(updateCategoryTypeDto, invalidCategoryTypeId)).thenThrow(new RuntimeException("CategoryType with ID " + invalidCategoryTypeId + " not found"));

        //Act & Assert
        mockMvc.perform(put("/category/type/" + invalidCategoryTypeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateCategoryTypeDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("CategoryType with ID " + invalidCategoryTypeId + " not found"));

        //Verify
        verify(categoryTypeService, times(1)).update(updateCategoryTypeDto, invalidCategoryTypeId);
    }

    @Test
    void testUpdateCategoryType_InvalidPercentage_Failure() throws Exception{
        //Arrange
        Long categoryTypeId = 10L;
        UpdateCategoryTypeDto invalidUpdateCategoryTypeDto = UpdateCategoryTypeDto.builder()
                .amountAllocated(100)
                .budgetAllocationPercentage(0)
                .savedAmount(15)
                .build();

        //Act & Assert
        mockMvc.perform(put("/category/type/" + categoryTypeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUpdateCategoryTypeDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("The provided CategoryType percent allocation is invalid"));
    }

    @Test
    void testUpdateCategoryType_InvalidSavedAmount_Failure() throws Exception{
        //Arrange
        Long categoryTypeId = 10L;
        UpdateCategoryTypeDto invalidUpdateCategoryTypeDto = UpdateCategoryTypeDto.builder()
                .amountAllocated(100)
                .budgetAllocationPercentage(.5)
                .savedAmount(-1)
                .build();

        //Act & Assert
        mockMvc.perform(put("/category/type/" + categoryTypeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdateCategoryTypeDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("The provided CategoryType saved amount is invalid"));
    }

    @Test
    void testUpdateCategoryType_InvalidAmountAllocated_Failure() throws Exception{
        //Arrange
        Long categoryTypeId = 10L;
        UpdateCategoryTypeDto invalidUpdateCategoryTypeDto = UpdateCategoryTypeDto.builder()
                .amountAllocated(-1)
                .budgetAllocationPercentage(.5)
                .savedAmount(0)
                .build();

        //Act & Assert
        mockMvc.perform(put("/category/type/" + categoryTypeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdateCategoryTypeDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("The provided CategoryType allocation amount is invalid"));
    }

}
