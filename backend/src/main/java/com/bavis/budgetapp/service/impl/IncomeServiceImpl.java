package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.dao.IncomeRepository;
import com.bavis.budgetapp.dto.IncomeDto;
import com.bavis.budgetapp.mapper.IncomeMapper;
import com.bavis.budgetapp.entity.Income;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.service.IncomeService;
import com.bavis.budgetapp.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Kellen Bavis
 *
 * Implementation of our Income Service functionality
 */
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

        //Map DTO to Income Entity
        User currentUser = _userService.getCurrentAuthUser();
        Income income = _incomeMapper.toIncome(incomeDto);
        income.setUser(currentUser);
        income.setUpdatedAt(LocalDateTime.now());

        log.info("Saving the following Income: [{}]", income);
        return _incomeRepository.save(income);
    }

    @Override
    public List<Income> readAll() {
        log.info("Retrieving all Income entities corresponding to authenticated user");
        User authenticatedUser = _userService.getCurrentAuthUser();
        return _incomeRepository.findByUserUserId(authenticatedUser.getUserId());
    }

    //TODO: finish impl and add comments
    @Override
    public Income readById(Long incomeId) {
        return null;
    }


    //TODO: consider removing and just using readAll() functionality above
    @Override
    public List<Income> readByUserId(Long userId) {
        log.info("Attempting to find Income[s] for User with ID {}", userId);
        return _incomeRepository.findByUserUserId(userId);
    }

    @Override
    public double findUserTotalIncomeAmount(Long userId) {
        log.info("Calculating total User monthly income for User with ID {}", userId);
        return readByUserId(userId).stream()
                .map(Income::getAmount)
                .reduce(0.0, Double::sum);
    }

    //TODO: complete and add comments/logging
    @Override
    public Income update(Income income, Long incomeId) {
        return null;
    }

    //TODO: complete and add comments/logging
    @Override
    public void detete(Long incomeId) {

    }
}
