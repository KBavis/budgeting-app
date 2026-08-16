package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.request.CategoryTypeDto;
import com.bavis.budgetapp.dto.request.UpdateCategoryTypeDto;
import com.bavis.budgetapp.dto.response.CategoryTypeResponseDto;
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

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        // Arrange
        CategoryTypeResponseDto categoryTypeResponseDto = CategoryTypeResponseDto.builder()
                .categoryTypeId(10L)
                .name("Needs")
                .budgetAmount(1000.0)
                .budgetAllocationPercentage(.5)
                .savedAmount(100.0)
                .build();

        // Mock
        when(categoryTypeService.get(eq(10L), any())).thenReturn(categoryTypeResponseDto);

        // Act
        ResultActions resultActions = mockMvc.perform(get("/category-type/" + categoryTypeResponseDto.getCategoryTypeId()));

        // Assert
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.categoryTypeId").value(categoryTypeResponseDto.getCategoryTypeId()))
                .andExpect(jsonPath("$.name").value(categoryTypeResponseDto.getName()))
                .andExpect(jsonPath("$.budgetAmount").value(categoryTypeResponseDto.getBudgetAmount()))
                .andExpect(jsonPath("$.budgetAllocationPercentage").value(categoryTypeResponseDto.getBudgetAllocationPercentage()))
                .andExpect(jsonPath("$.savedAmount").value(categoryTypeResponseDto.getSavedAmount()));
    }

    @Test
    void testRead_IdNotFound_Failure() throws Exception {
        // Arrange
        long categoryTypeId = 1L;
        RuntimeException runtimeException = new RuntimeException("Invalid category type id: " + categoryTypeId);

        // Mock
        when(categoryTypeService.get(eq(1L), any())).thenThrow(runtimeException);

        // Act
        ResultActions resultActions = mockMvc.perform(get("/category-type/" + categoryTypeId));

        // Assert
        resultActions
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Invalid category type id: " + categoryTypeId));
    }

    @Test
    void testReadAll_Successful() throws Exception {
        // Arrange
        CategoryTypeResponseDto dtoOne = CategoryTypeResponseDto.builder()
                .categoryTypeId(10L)
                .name("Needs")
                .budgetAllocationPercentage(.5)
                .build();

        CategoryTypeResponseDto dtoTwo = CategoryTypeResponseDto.builder()
                .categoryTypeId(11L)
                .name("Wants")
                .budgetAllocationPercentage(.2)
                .build();

        CategoryTypeResponseDto dtoThree = CategoryTypeResponseDto.builder()
                .categoryTypeId(12L)
                .name("Investments")
                .budgetAllocationPercentage(.3)
                .build();

        List<CategoryTypeResponseDto> expectedCategoryTypes = List.of(dtoOne, dtoTwo, dtoThree);

        // Mock
        when(categoryTypeService.getAll(any())).thenReturn(expectedCategoryTypes);

        // Act & Assert
        ResultActions resultActions = mockMvc.perform(get("/category-type"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].categoryTypeId").value(dtoOne.getCategoryTypeId()))
                .andExpect(jsonPath("$[0].name").value(dtoOne.getName()))
                .andExpect(jsonPath("$[0].budgetAllocationPercentage").value(dtoOne.getBudgetAllocationPercentage()))
                .andExpect(jsonPath("$[1].categoryTypeId").value(dtoTwo.getCategoryTypeId()))
                .andExpect(jsonPath("$[1].name").value(dtoTwo.getName()))
                .andExpect(jsonPath("$[1].budgetAllocationPercentage").value(dtoTwo.getBudgetAllocationPercentage()))
                .andExpect(jsonPath("$[2].categoryTypeId").value(dtoThree.getCategoryTypeId()))
                .andExpect(jsonPath("$[2].name").value(dtoThree.getName()))
                .andExpect(jsonPath("$[2].budgetAllocationPercentage").value(dtoThree.getBudgetAllocationPercentage()));

        // Verify
        verify(categoryTypeService, times(1)).getAll(any());
    }

    @Test
    public void testBulkCreateCategoryType_ValidRequest_Successful() throws Exception {
        // Arrange
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

        CategoryTypeResponseDto dto1 = CategoryTypeResponseDto.builder()
                .categoryTypeId(1L)
                .name("Category Type One")
                .budgetAllocationPercentage(.5)
                .budgetAmount(5000)
                .build();

        CategoryTypeResponseDto dto2 = CategoryTypeResponseDto.builder()
                .categoryTypeId(2L)
                .name("Category Type Two")
                .budgetAllocationPercentage(.25)
                .budgetAmount(2500)
                .build();

        CategoryTypeResponseDto dto3 = CategoryTypeResponseDto.builder()
                .categoryTypeId(3L)
                .name("Category Type Three")
                .budgetAllocationPercentage(.25)
                .budgetAmount(2500)
                .build();

        List<CategoryTypeResponseDto> responseDtos = List.of(dto1, dto2, dto3);

        // Mock
        when(categoryTypeService.createMany(categoryTypeDtos)).thenReturn(responseDtos);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/category-type/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryTypeDtos)));

        // Assert
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].categoryTypeId").value(dto1.getCategoryTypeId()))
                .andExpect(jsonPath("$[0].name").value(dto1.getName()))
                .andExpect(jsonPath("$[0].budgetAmount").value(dto1.getBudgetAmount()))
                .andExpect(jsonPath("$[0].budgetAllocationPercentage").value(dto1.getBudgetAllocationPercentage()))
                .andExpect(jsonPath("$[1].categoryTypeId").value(dto2.getCategoryTypeId()))
                .andExpect(jsonPath("$[1].name").value(dto2.getName()))
                .andExpect(jsonPath("$[1].budgetAmount").value(dto2.getBudgetAmount()))
                .andExpect(jsonPath("$[1].budgetAllocationPercentage").value(dto2.getBudgetAllocationPercentage()))
                .andExpect(jsonPath("$[2].categoryTypeId").value(dto3.getCategoryTypeId()))
                .andExpect(jsonPath("$[2].name").value(dto3.getName()))
                .andExpect(jsonPath("$[2].budgetAmount").value(dto3.getBudgetAmount()))
                .andExpect(jsonPath("$[2].budgetAllocationPercentage").value(dto3.getBudgetAllocationPercentage()));
    }

    @Test
    void testUpdateCategoryType_ValidRequest_Success() throws Exception {
        // Arrange
        Long categoryTypeId = 10L;
        UpdateCategoryTypeDto updateCategoryTypeDto = UpdateCategoryTypeDto.builder()
                .amountAllocated(100.0)
                .budgetAllocationPercentage(.5)
                .savedAmount(15.0)
                .build();

        CategoryTypeResponseDto expectedResponseDto = CategoryTypeResponseDto.builder()
                .categoryTypeId(categoryTypeId)
                .savedAmount(15.0)
                .budgetAllocationPercentage(.5)
                .budgetAmount(100.0)
                .build();

        // Mock
        when(categoryTypeService.update(updateCategoryTypeDto, categoryTypeId)).thenReturn(expectedResponseDto);

        // Act
        ResultActions resultActions = mockMvc.perform(put("/category-type/" + categoryTypeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateCategoryTypeDto)));

        // Assert
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.categoryTypeId").value(categoryTypeId))
                .andExpect(jsonPath("$.savedAmount").value(updateCategoryTypeDto.getSavedAmount()))
                .andExpect(jsonPath("$.budgetAllocationPercentage").value(updateCategoryTypeDto.getBudgetAllocationPercentage()))
                .andExpect(jsonPath("$.budgetAmount").value(updateCategoryTypeDto.getAmountAllocated()));

        // Verify
        verify(categoryTypeService, times(1)).update(updateCategoryTypeDto, categoryTypeId);
    }

    @Test
    void testUpdateCategoryType_NotFound_Failure() throws Exception {
        // Arrange
        Long invalidCategoryTypeId = 10L;
        UpdateCategoryTypeDto updateCategoryTypeDto = UpdateCategoryTypeDto.builder()
                .amountAllocated(100.0)
                .budgetAllocationPercentage(.5)
                .savedAmount(15.0)
                .build();

        // Mock
        when(categoryTypeService.update(updateCategoryTypeDto, invalidCategoryTypeId)).thenThrow(new RuntimeException("CategoryType with ID " + invalidCategoryTypeId + " not found"));

        // Act & Assert
        mockMvc.perform(put("/category-type/" + invalidCategoryTypeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateCategoryTypeDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("CategoryType with ID " + invalidCategoryTypeId + " not found"));

        // Verify
        verify(categoryTypeService, times(1)).update(updateCategoryTypeDto, invalidCategoryTypeId);
    }

    @Test
    void testUpdateCategoryType_InvalidPercentage_Failure() throws Exception {
        // Arrange
        Long categoryTypeId = 10L;
        UpdateCategoryTypeDto invalidUpdateCategoryTypeDto = UpdateCategoryTypeDto.builder()
                .amountAllocated(100.0)
                .budgetAllocationPercentage(0.0)
                .savedAmount(15.0)
                .build();

        // Act & Assert
        mockMvc.perform(put("/category-type/" + categoryTypeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUpdateCategoryTypeDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("The provided CategoryType percent allocation is invalid"));
    }

    @Test
    void testUpdateCategoryType_InvalidSavedAmount_Failure() throws Exception {
        // Arrange
        Long categoryTypeId = 10L;
        UpdateCategoryTypeDto invalidUpdateCategoryTypeDto = UpdateCategoryTypeDto.builder()
                .amountAllocated(100.0)
                .budgetAllocationPercentage(.5)
                .savedAmount(-1.0)
                .build();

        // Act & Assert
        mockMvc.perform(put("/category-type/" + categoryTypeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUpdateCategoryTypeDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("The provided CategoryType saved amount is invalid"));
    }

    @Test
    void testUpdateCategoryType_InvalidAmountAllocated_Failure() throws Exception {
        // Arrange
        Long categoryTypeId = 10L;
        UpdateCategoryTypeDto invalidUpdateCategoryTypeDto = UpdateCategoryTypeDto.builder()
                .amountAllocated(-1.0)
                .budgetAllocationPercentage(.5)
                .savedAmount(0.0)
                .build();

        // Act & Assert
        mockMvc.perform(put("/category-type/" + categoryTypeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUpdateCategoryTypeDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("The provided CategoryType allocation amount is invalid"));
    }
}
