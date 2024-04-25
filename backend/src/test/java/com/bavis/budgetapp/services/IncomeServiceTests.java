package com.bavis.budgetapp.services;

import com.bavis.budgetapp.dao.IncomeRepository;
import com.bavis.budgetapp.dto.IncomeDTO;
import com.bavis.budgetapp.enumeration.IncomeSource;
import com.bavis.budgetapp.enumeration.IncomeType;
import com.bavis.budgetapp.mapper.IncomeMapper;
import com.bavis.budgetapp.model.Income;
import com.bavis.budgetapp.model.User;
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

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

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

    @InjectMocks
    private IncomeServiceImpl incomeService;

    private IncomeDTO incomeDTO;

    private Income income;

    private User user;


    @BeforeEach
    public void setup() {

        incomeDTO = IncomeDTO.builder()
                .incomeSource(IncomeSource.EMPLOYER)
                .incomeType(IncomeType.SALARY)
                .amount(5000.0)
                .description("Bi-weekly salary from Company")
                .build();

        income = Income.builder()
                .incomeSource(IncomeSource.EMPLOYER)
                .incomeType(IncomeType.SALARY)
                .amount(5000.0)
                .description("Bi-weekly salary from Company")
                .incomeId(1L)
                .build();

        user = User.builder()
                .userId(10L)
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
    }

}
