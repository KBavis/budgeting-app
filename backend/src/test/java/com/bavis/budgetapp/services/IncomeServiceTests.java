package com.bavis.budgetapp.services;

import com.bavis.budgetapp.dao.IncomeRepository;
import com.bavis.budgetapp.dto.request.IncomeDto;
import com.bavis.budgetapp.constants.IncomeSource;
import com.bavis.budgetapp.constants.IncomeType;
import com.bavis.budgetapp.dto.request.UpdateCategoryTypeDto;
import com.bavis.budgetapp.dto.request.UpdateIncomeDto;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.mapper.IncomeMapper;
import com.bavis.budgetapp.entity.Income;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.service.CategoryTypeService;
import com.bavis.budgetapp.service.impl.IncomeServiceImpl;
import com.bavis.budgetapp.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.bavis.budgetapp.entity.CategoryTypeVt;
import com.bavis.budgetapp.entity.IncomeVt;
import com.bavis.budgetapp.service.EffectivityService;
import com.bavis.budgetapp.dto.response.IncomeResponseDto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles(profiles = "test")
public class IncomeServiceTests {

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private IncomeMapper incomeMapper;

    @Mock
    private CategoryTypeService categoryTypeService;

    @Mock
    private EffectivityService effectivityService;

    @InjectMocks
    private IncomeServiceImpl incomeService;

    private IncomeDto incomeDTO;

    private Income income;

    private IncomeVt incomeVt;

    private IncomeResponseDto incomeResponseDto;

    private User user;


    @BeforeEach
    public void setup() {

        incomeDTO = IncomeDto.builder()
                .incomeSource(IncomeSource.EMPLOYER)
                .incomeType(IncomeType.SALARY)
                .amount(5000.0)
                .description("Bi-weekly salary from Company")
                .build();

        user = User.builder()
                .userId(10L)
                .build();

        income = Income.builder()
                .incomeId(1L)
                .user(user)
                .build();

        incomeVt = IncomeVt.builder()
                .income(income)
                .incomeSource(IncomeSource.EMPLOYER)
                .incomeType(IncomeType.SALARY)
                .amount(5000.0)
                .description("Bi-weekly salary from Company")
                .build();

        incomeResponseDto = IncomeResponseDto.builder()
                .incomeId(1L)
                .incomeSource(IncomeSource.EMPLOYER)
                .incomeType(IncomeType.SALARY)
                .amount(5000.0)
                .description("Bi-weekly salary from Company")
                .build();
    }

    @Test
    public void testCreate_Successful() {
        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(incomeRepository.save(any(Income.class))).thenReturn(income);
        when(effectivityService.getActiveVt(any(), any())).thenReturn(incomeVt);
        when(incomeMapper.toResponseDto(income, incomeVt)).thenReturn(incomeResponseDto);

        //Act
        IncomeResponseDto actualResponse = incomeService.create(incomeDTO);

        //Assert
        assertNotNull(actualResponse);
        assertEquals(incomeResponseDto.getIncomeId(), actualResponse.getIncomeId());
        assertEquals(incomeResponseDto.getIncomeSource(), actualResponse.getIncomeSource());
        assertEquals(incomeResponseDto.getIncomeType(), actualResponse.getIncomeType());
        assertEquals(incomeResponseDto.getDescription(), actualResponse.getDescription());
        assertEquals(incomeResponseDto.getAmount(), actualResponse.getAmount());

        //Verify
        verify(userService, times(1)).getCurrentAuthUser();
        verify(incomeRepository, times(1)).save(any(Income.class));
    }

    @Test
    public void testReadByUserId_Successful() {
        //Arrange
        List<Income> incomes = List.of(income, income, income);

        //Mock
        when(incomeRepository.findByUserUserIdAndAsOf(eq(user.getUserId()), any(LocalDate.class))).thenReturn(incomes);

        //Act
        List<Income> foundIncomes = incomeService.findAllEntitiesByUserId(user.getUserId(), null);

        //Assert
        assertNotNull(foundIncomes);
        assertEquals(3, foundIncomes.size());
        for(Income foundIncome: foundIncomes) {
            assertEquals(income.getIncomeId(), foundIncome.getIncomeId());
        }
    }

    @Test
    void testReadAll_Successful() {
        //Arrange
        List<Income> incomes = List.of(income, income, income);

        //Mock
        when(incomeRepository.findByUserUserIdAndAsOf(eq(user.getUserId()), any(LocalDate.class))).thenReturn(incomes);
        when(userService.getCurrentAuthUser()).thenReturn(user);

        //Act
        List<Income> foundIncomes = incomeService.findAllEntities(null);

        //Assert
        assertNotNull(foundIncomes);
        assertEquals(3, foundIncomes.size());
        for(Income foundIncome: foundIncomes) {
            assertEquals(income.getIncomeId(), foundIncome.getIncomeId());
        }
    }

    @Test
    public void testFindUserTotalIncomeAmount_Successful() {
        //Arrange
        double expectedAmount = 15000.0;
        List<Income> incomes = List.of(income, income, income);

        //Mock
        when(incomeRepository.findByUserUserIdAndAsOf(eq(user.getUserId()), any())).thenReturn(incomes);
        when(effectivityService.getActiveVt(any(), any())).thenReturn(incomeVt);

        //Act
        double totalAmount = incomeService.findUserTotalIncomeAmount(user.getUserId(), null);

        //Assert
        assertEquals(expectedAmount, totalAmount);
    }


    @Test
    void testUpdate_returnsExpectedIncome() {
        //Arrange
        UpdateIncomeDto updateIncomeDto = UpdateIncomeDto.builder()
                .incomeId(1L)
                .amount(2912)
                .build();
        CategoryType categoryType = CategoryType.builder()
                .categoryTypeId(1L)
                .build();
        CategoryTypeVt ctVt = CategoryTypeVt.builder()
                .savedAmount(200)
                .budgetAmount(1000)
                .budgetAllocationPercentage(.50)
                .build();
        List<CategoryType> categoryTypes = List.of(categoryType);

        IncomeResponseDto updatedResponse = IncomeResponseDto.builder()
                .incomeId(1L)
                .amount(2912)
                .build();

        //Mock
        when(categoryTypeService.findAllEntities(null)).thenReturn(categoryTypes);
        when(incomeRepository.findByIncomeIdAndAsOf(eq(1L), any())).thenReturn(Optional.of(income));
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(effectivityService.getActiveVt(any(), any())).thenReturn(incomeVt).thenReturn(ctVt).thenReturn(incomeVt);
        when(categoryTypeService.update(any(UpdateCategoryTypeDto.class), any(Long.class))).thenReturn(null);
        when(incomeRepository.saveAndFlush(any())).thenReturn(income);
        when(incomeMapper.toResponseDto(any(), any())).thenReturn(updatedResponse);

        //Act
        IncomeResponseDto actualIncome = incomeService.update(updateIncomeDto);

        //Assert
        assertEquals(actualIncome.getAmount(), updateIncomeDto.getAmount());
    }

    @Test
    void testUpdate_throwExceptionIfNonAuthUser() {
        //Arrange
        User invalidUser = User.builder()
                .userId(30204L)
                .build();
        income.setUser(invalidUser);
        UpdateIncomeDto updateIncomeDto = UpdateIncomeDto.builder()
                .incomeId(1L)
                .amount(2912)
                .build();

        //Mock
        when(incomeRepository.findByIncomeIdAndAsOf(eq(1L), any())).thenReturn(Optional.of(income));
        when(userService.getCurrentAuthUser()).thenReturn(user);

        //Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            incomeService.update(updateIncomeDto);
        });
        assertEquals("Unable to update user Income due to user not being owner of specified income", exception.getMessage());
    }

    @Test
    void testUpdate_UpdatesCategoryTypesCorrectly() {
        //Arrange
        UpdateIncomeDto updateIncomeDto = UpdateIncomeDto.builder()
                .incomeId(1L)
                .amount(2912)
                .build();
        CategoryType categoryType = CategoryType.builder()
                .categoryTypeId(1L)
                .build();
        CategoryTypeVt ctVt = CategoryTypeVt.builder()
                .savedAmount(200)
                .budgetAmount(1000)
                .budgetAllocationPercentage(.50)
                .build();
        List<CategoryType> categoryTypes = List.of(categoryType);

        UpdateCategoryTypeDto expectedDto = UpdateCategoryTypeDto.builder()
                .budgetAllocationPercentage(ctVt.getBudgetAllocationPercentage())
                .build();

        //Mock
        when(categoryTypeService.findAllEntities(null)).thenReturn(categoryTypes);
        when(incomeRepository.findByIncomeIdAndAsOf(eq(1L), any())).thenReturn(Optional.of(income));
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(effectivityService.getActiveVt(any(), any())).thenReturn(incomeVt).thenReturn(ctVt).thenReturn(incomeVt);
        when(categoryTypeService.update(any(UpdateCategoryTypeDto.class), any(Long.class))).thenReturn(null);
        when(incomeRepository.saveAndFlush(any())).thenReturn(income);

        //Act
        incomeService.update(updateIncomeDto);

        //Verify
        verify(categoryTypeService, times(1)).update(expectedDto, categoryType.getCategoryTypeId());
    }



}
