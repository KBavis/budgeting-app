package com.bavis.budgetapp.service;

import com.bavis.budgetapp.dto.IncomeDto;
import com.bavis.budgetapp.model.Income;

public interface IncomeService {
    Income create(IncomeDto income);

    Income readById(Long incomeId);

    Income readByUserId(Long userId);

    Income update(Income income, Long incomeId);

    void detete(Long incomeId);


}
