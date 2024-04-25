package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.dao.IncomeRepository;
import com.bavis.budgetapp.dto.IncomeDto;
import com.bavis.budgetapp.mapper.IncomeMapper;
import com.bavis.budgetapp.model.Income;
import com.bavis.budgetapp.model.User;
import com.bavis.budgetapp.service.IncomeService;
import com.bavis.budgetapp.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Log4j2
public class IncomeServiceImpl implements IncomeService {

    private IncomeRepository _incomeRepository;

    private UserService _userService;

    private IncomeMapper _incomeMapper;

    public IncomeServiceImpl(IncomeRepository _incomeRepository, UserService _userService, IncomeMapper _incomeMapper){
        this._incomeRepository = _incomeRepository;
        this._userService = _userService;
        this._incomeMapper = _incomeMapper;
    }
    @Override
    public Income create(IncomeDto incomeDto) {
        log.info("Creating Income: [{}]", incomeDto);

        User currentUser = _userService.getCurrentAuthUser();
        Income income = _incomeMapper.toIncome(incomeDto);
        income.setUser(currentUser);
        income.setUpdatedAt(LocalDateTime.now());

        return _incomeRepository.save(income);
    }

    @Override
    public Income readById(Long incomeId) {
        return null;
    }

    @Override
    public Income readByUserId(Long userId) {
        return null;
    }

    @Override
    public Income update(Income income, Long incomeId) {
        return null;
    }

    @Override
    public void detete(Long incomeId) {

    }
}
