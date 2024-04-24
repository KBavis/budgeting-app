package com.bavis.budgetapp.service;

import com.bavis.budgetapp.dto.IncomeDTO;
import com.bavis.budgetapp.model.Income;
import org.springframework.stereotype.Service;

public interface IncomeService {
    Income create(IncomeDTO income);

    Income readById(Long incomeId);

    Income readByUserId(Long userId);

    Income update(Income income, Long incomeId);

    void detete(Long incomeId);


}
