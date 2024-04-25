package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.IncomeDto;
import com.bavis.budgetapp.enumeration.IncomeSource;
import com.bavis.budgetapp.enumeration.IncomeType;
import com.bavis.budgetapp.model.Income;
import com.bavis.budgetapp.model.User;
import com.bavis.budgetapp.service.impl.IncomeServiceImpl;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
@ActiveProfiles(profiles = "test")
public class IncomeContollerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IncomeServiceImpl incomeService;

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

        resultActions.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
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
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
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
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
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
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
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
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("incomeSource must not be null"));
    }



}
