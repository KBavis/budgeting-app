package com.bavis.budgetapp.service;

import com.bavis.budgetapp.dto.IncomeDto;
import com.bavis.budgetapp.entity.Income;

import java.util.List;

public interface IncomeService {
    Income create(IncomeDto income);

    Income readById(Long incomeId);

    List<Income> readByUserId(Long userId);

    Income update(Income income, Long incomeId);

    double findUserTotalIncomeAmount(Long userId);

    void detete(Long incomeId);


}
