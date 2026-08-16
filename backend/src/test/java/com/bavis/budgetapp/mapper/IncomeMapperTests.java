package com.bavis.budgetapp.mapper;

import com.bavis.budgetapp.constants.IncomeSource;
import com.bavis.budgetapp.constants.IncomeType;
import com.bavis.budgetapp.dto.response.IncomeResponseDto;
import com.bavis.budgetapp.entity.Income;
import com.bavis.budgetapp.entity.IncomeVt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ContextConfiguration(classes = {IncomeMapperImpl.class})
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
public class IncomeMapperTests {

    @Autowired
    private IncomeMapper incomeMapper;

    @Test
    public void testToResponseDto_Successful() {
        //Arrange
        Income income = Income.builder()
                .incomeId(1L)
                .build();
        IncomeVt incomeVt = IncomeVt.builder()
                .income(income)
                .incomeSource(IncomeSource.EMPLOYER)
                .incomeType(IncomeType.CAPITAL_GAINS)
                .amount(1000.0)
                .description("Income Description")
                .build();

        //Act
        IncomeResponseDto responseDto = incomeMapper.toResponseDto(income, incomeVt);

        //Assert
        assertNotNull(responseDto);
        assertEquals(income.getIncomeId(), responseDto.getIncomeId());
        Assertions.assertEquals(incomeVt.getAmount(), responseDto.getAmount(), .001);
        assertEquals(incomeVt.getIncomeSource(), responseDto.getIncomeSource());
        assertEquals(incomeVt.getIncomeType(), responseDto.getIncomeType());
        assertEquals(incomeVt.getDescription(), responseDto.getDescription());
    }
}
