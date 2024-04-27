package com.bavis.budgetapp.services;


import com.bavis.budgetapp.dao.CategoryTypeRepository;
import com.bavis.budgetapp.dto.CategoryTypeDto;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.mapper.CategoryTypeMapper;
import com.bavis.budgetapp.service.IncomeService;
import com.bavis.budgetapp.service.UserService;
import com.bavis.budgetapp.service.impl.CategoryTypeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

    @BeforeEach
    public void setup() {
        user = User.builder()
                .userId(10L)
                .build();

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
    public void testCreateMany_Failure() {

    }
}
