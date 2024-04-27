package com.bavis.budgetapp.mapper;

import com.bavis.budgetapp.dto.IncomeDto;
import com.bavis.budgetapp.constants.IncomeSource;
import com.bavis.budgetapp.constants.IncomeType;
import com.bavis.budgetapp.entity.Income;
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

@ContextConfiguration(classes = {IncomeMapperImpl.class})
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
public class IncomeMapperTests {

    @Autowired
    private IncomeMapper incomeMapper;

    @Test
    public void testToIncome_Successful() {
        //Arrange
        IncomeDto incomeDto = IncomeDto.builder()
                .incomeSource(IncomeSource.EMPLOYER)
                .incomeType(IncomeType.CAPITAL_GAINS)
                .amount(1000.0)
                .description("Income Description")
                .build();
        Income income = new Income();

        //Act
        income = incomeMapper.toIncome(incomeDto);

        //Assert
        Assertions.assertEquals(incomeDto.getAmount(), income.getAmount(), .001);
        assertEquals(incomeDto.getIncomeSource(), income.getIncomeSource());
        assertEquals(incomeDto.getIncomeType(), income.getIncomeType());
        assertEquals(incomeDto.getDescription(), income.getDescription());
    }
}
