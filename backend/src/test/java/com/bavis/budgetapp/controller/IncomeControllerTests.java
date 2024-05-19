package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.IncomeDto;
import com.bavis.budgetapp.constants.IncomeSource;
import com.bavis.budgetapp.constants.IncomeType;
import com.bavis.budgetapp.entity.Income;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.service.impl.IncomeServiceImpl;
import com.bavis.budgetapp.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
@ActiveProfiles(profiles = "test")
public class IncomeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IncomeServiceImpl incomeService;

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    private ObjectMapper objectMapper;

    private IncomeDto validIncomeDto;

    private Income income;

    private User user;

    private LocalDateTime localDateTime;

    @BeforeEach
    void setup() {
        user = User.builder()
                .userId(10L)
                .build();

       validIncomeDto = IncomeDto.builder()
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
               .user(user)
               .updatedAt(localDateTime)
               .build();
    }

    @Test
    public void testCreate_ValidRequest_Successful() throws Exception{
        //Mock
        when(incomeService.create(validIncomeDto)).thenReturn(income);

        //Act
        ResultActions resultActions = mockMvc.perform(post("/income")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validIncomeDto)));

        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.incomeSource").value(income.getIncomeSource().toString()))
                .andExpect(jsonPath("$.incomeType").value(income.getIncomeType().toString()))
                .andExpect(jsonPath("$.amount").value(income.getAmount()))
                .andExpect(jsonPath("$.description").value(income.getDescription()))
                .andExpect(jsonPath("$.incomeId").value(income.getIncomeId()))
                .andExpect(jsonPath("$.updatedAt").value(income.getUpdatedAt()));

        verify(incomeService, times(1)).create(validIncomeDto);
    }

    @Test
    void testReadAll_Successful() throws Exception{
        //Arrange
        Income incomeTwo = Income.builder()
                .incomeSource(IncomeSource.STOCK)
                .incomeType(IncomeType.CAPITAL_GAINS)
                .amount(3000.0)
                .description("Stock dividends")
                .incomeId(2L)
                .user(user)
                .updatedAt(localDateTime)
                .build();

        Income incomeThree = Income.builder()
                .incomeSource(IncomeSource.BOND)
                .incomeType(IncomeType.CAPITAL_GAINS)
                .amount(8000.0)
                .description("Bond dividends")
                .incomeId(3L)
                .user(user)
                .updatedAt(localDateTime)
                .build();

        List<Income> expectedIncomes = List.of(income, incomeTwo, incomeThree);

        //Mock
        when(incomeService.readAll()).thenReturn(expectedIncomes);

        //Act & Assert
        ResultActions resultActions = mockMvc.perform(get("/income"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].incomeSource").value(income.getIncomeSource().toString()))
                .andExpect(jsonPath("$[0].incomeType").value(income.getIncomeType().toString()))
                .andExpect(jsonPath("$[0].amount").value(income.getAmount()))
                .andExpect(jsonPath("$[0].description").value(income.getDescription()))
                .andExpect(jsonPath("$[0].incomeId").value(income.getIncomeId()))
                .andExpect(jsonPath("$[0].updatedAt").value(income.getUpdatedAt()))
                .andExpect(jsonPath("$[1].incomeSource").value(incomeTwo.getIncomeSource().toString()))
                .andExpect(jsonPath("$[1].incomeType").value(incomeTwo.getIncomeType().toString()))
                .andExpect(jsonPath("$[1].amount").value(incomeTwo.getAmount()))
                .andExpect(jsonPath("$[1].description").value(incomeTwo.getDescription()))
                .andExpect(jsonPath("$[1].incomeId").value(incomeTwo.getIncomeId()))
                .andExpect(jsonPath("$[1].updatedAt").value(incomeTwo.getUpdatedAt()))
                .andExpect(jsonPath("$[2].incomeSource").value(incomeThree.getIncomeSource().toString()))
                .andExpect(jsonPath("$[2].incomeType").value(incomeThree.getIncomeType().toString()))
                .andExpect(jsonPath("$[2].amount").value(incomeThree.getAmount()))
                .andExpect(jsonPath("$[2].description").value(incomeThree.getDescription()))
                .andExpect(jsonPath("$[2].incomeId").value(incomeThree.getIncomeId()))
                .andExpect(jsonPath("$[2].updatedAt").value(incomeThree.getUpdatedAt()));

        //Verify
        verify(incomeService, times(1)).readAll();
    }

    @Test
    public void testCreate_InvalidAmount_Failure() throws Exception {
        //Arrange
        IncomeDto invalidAmountDto = IncomeDto.builder()
                .amount(-1)
                .description("test description")
                .incomeType(IncomeType.CAPITAL_GAINS)
                .incomeSource(IncomeSource.EMPLOYER)
                .build();
        //Act
        ResultActions resultActions = mockMvc.perform(post("/income")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAmountDto)));

        //Assert
        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("The provided income amount is not valid"));
    }

    @Test
    public void testCreate_EmptyDescription_Failure() throws Exception{
        //Arrange
        IncomeDto invalidAmountDto = IncomeDto.builder()
                .amount(1000)
                .description("")
                .incomeType(IncomeType.CAPITAL_GAINS)
                .incomeSource(IncomeSource.EMPLOYER)
                .build();
        //Act
        ResultActions resultActions = mockMvc.perform(post("/income")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAmountDto)));

        //Assert
        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("description must not be empty"));
    }

    @Test
    public void testCreate_NullIncomeType_Failure() throws Exception{
        //Arrange
        IncomeDto invalidAmountDto = IncomeDto.builder()
                .amount(1000)
                .description("description")
                .incomeType(null)
                .incomeSource(IncomeSource.EMPLOYER)
                .build();
        //Act
        ResultActions resultActions = mockMvc.perform(post("/income")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAmountDto)));

        //Assert
        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("incomeType must not be null"));
    }

    @Test
    public void testCreate_NullIncomeSource_Failure() throws Exception{
        //Arrange
        IncomeDto invalidAmountDto = IncomeDto.builder()
                .amount(1000)
                .description("description")
                .incomeType(IncomeType.CAPITAL_GAINS)
                .incomeSource(null)
                .build();
        //Act
        ResultActions resultActions = mockMvc.perform(post("/income")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAmountDto)));

        //Assert
        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("incomeSource must not be null"));
    }



}
