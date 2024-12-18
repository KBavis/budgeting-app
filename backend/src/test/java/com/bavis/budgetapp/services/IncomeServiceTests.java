package com.bavis.budgetapp.services;

import com.bavis.budgetapp.dao.IncomeRepository;
import com.bavis.budgetapp.dto.IncomeDto;
import com.bavis.budgetapp.constants.IncomeSource;
import com.bavis.budgetapp.constants.IncomeType;
import com.bavis.budgetapp.dto.UpdateCategoryTypeDto;
import com.bavis.budgetapp.dto.UpdateIncomeDto;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    @InjectMocks
    private IncomeServiceImpl incomeService;

    private IncomeDto incomeDTO;

    private Income income;

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
                .incomeSource(IncomeSource.EMPLOYER)
                .incomeType(IncomeType.SALARY)
                .amount(5000.0)
                .description("Bi-weekly salary from Company")
                .incomeId(1L)
                .user(user)
                .build();
    }

    @Test
    public void testCreate_Successful() {
       //Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(incomeMapper.toIncome(incomeDTO)).thenReturn(income);
        when(incomeRepository.save(income)).thenReturn(income);

        //Act
        Income actualIncome = incomeService.create(incomeDTO);

        //Assert
        assertNotNull(actualIncome);
        assertEquals(income.getIncomeId(), actualIncome.getIncomeId());
        assertEquals(income.getIncomeSource(), actualIncome.getIncomeSource());
        assertEquals(income.getIncomeType(), actualIncome.getIncomeType());
        assertEquals(income.getUser().getUserId(), actualIncome.getUser().getUserId());
        assertEquals(income.getDescription(), actualIncome.getDescription());
        assertEquals(income.getAmount(), actualIncome.getAmount());
        assertNotNull(income.getUpdatedAt()); //not able to validate exact time

        //Verify
        verify(userService, times(1)).getCurrentAuthUser();
        verify(incomeMapper, times(1)).toIncome(incomeDTO);
        verify(incomeRepository, times(1)).save(income);
    }

    @Test
    public void testReadByUserId_Successful() {
        //Arrange
        List<Income> incomes = List.of(income, income, income);

        //Mock
        when(incomeRepository.findByUserUserId(user.getUserId())).thenReturn(incomes);

        //Act
        List<Income> foundIncomes = incomeService.readByUserId(user.getUserId());

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
        when(incomeRepository.findByUserUserId(user.getUserId())).thenReturn(incomes);
        when(userService.getCurrentAuthUser()).thenReturn(user);

        //Act
        List<Income> foundIncomes = incomeService.readAll();

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
        when(incomeRepository.findByUserUserId(user.getUserId())).thenReturn(incomes);

        //Act
        double totalAmount = incomeService.findUserTotalIncomeAmount(user.getUserId());

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
                .savedAmount(200)
                .budgetAmount(1000)
                .budgetAllocationPercentage(.50)
                .build();
        List<CategoryType> categoryTypes = List.of(categoryType);

        //Mock
        when(categoryTypeService.readAll()).thenReturn(categoryTypes);
        when(incomeRepository.findById(1L)).thenReturn(Optional.of(income));
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(categoryTypeService.update(any(UpdateCategoryTypeDto.class), any(Long.class))).thenReturn(null);

        //Act
        Income actualIncome = incomeService.update(updateIncomeDto);

        //Assert
        assertEquals(actualIncome.getAmount(), updateIncomeDto.getAmount());
        assertNotNull(actualIncome.getUpdatedAt());
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
        when(incomeRepository.findById(1L)).thenReturn(Optional.of(income));
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
                .savedAmount(200)
                .budgetAmount(1000)
                .budgetAllocationPercentage(.50)
                .build();
        List<CategoryType> categoryTypes = List.of(categoryType);

        UpdateCategoryTypeDto expectedDto = UpdateCategoryTypeDto.builder()
                .budgetAllocationPercentage(categoryType.getBudgetAllocationPercentage())
                .amountAllocated(updateIncomeDto.getAmount() * categoryType.getBudgetAllocationPercentage())
                .savedAmount((updateIncomeDto.getAmount() * categoryType.getBudgetAllocationPercentage()) - (categoryType.getBudgetAmount() - categoryType.getSavedAmount()))
                .build();

        //Mock
        when(categoryTypeService.readAll()).thenReturn(categoryTypes);
        when(incomeRepository.findById(1L)).thenReturn(Optional.of(income));
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(categoryTypeService.update(any(UpdateCategoryTypeDto.class), any(Long.class))).thenReturn(null);

        //Act
        incomeService.update(updateIncomeDto);

        //Verify
        verify(categoryTypeService, times(1)).update(expectedDto, categoryType.getCategoryTypeId());
    }



}
