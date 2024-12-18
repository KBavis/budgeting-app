package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.dao.IncomeRepository;
import com.bavis.budgetapp.dto.IncomeDto;
import com.bavis.budgetapp.dto.UpdateCategoryTypeDto;
import com.bavis.budgetapp.dto.UpdateIncomeDto;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.mapper.IncomeMapper;
import com.bavis.budgetapp.entity.Income;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.service.CategoryTypeService;
import com.bavis.budgetapp.service.IncomeService;
import com.bavis.budgetapp.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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

    private CategoryTypeService _categoryTypeService;

    public IncomeServiceImpl(IncomeRepository _incomeRepository, UserService _userService, IncomeMapper _incomeMapper, @Lazy CategoryTypeService _categoryTypeService){
        this._incomeRepository = _incomeRepository;
        this._userService = _userService;
        this._incomeMapper = _incomeMapper;
        this._categoryTypeService = _categoryTypeService;
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

    @Override
    public Income readById(Long incomeId) {
        log.info("Attempting to fetch income corresponding to the following ID: {}", incomeId);
        return _incomeRepository.findById(incomeId).orElseThrow(() -> new RuntimeException("Unable to locate Income with the following ID: " + incomeId));
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

    @Override
    public Income update(UpdateIncomeDto incomeDto) {
        log.info("Attempting to update user's Income via the following incomeDto: [{}]", incomeDto);
        User authUser = _userService.getCurrentAuthUser();

        //Fetch Income corresponding to IncomeDto
        Income incomeToUpdate = readById(incomeDto.getIncomeId());

        //Ensure Income corresponds to Authenticated User
        if(!Objects.equals(incomeToUpdate.getUser().getUserId(), authUser.getUserId())){
            log.error("Error: Non-auth user is attempting to update income not owned by them");
            throw new RuntimeException("Unable to update user Income due to user not being owner of specified income");
        }

        //Fetch All Category Types corresponding to user
        List<CategoryType> categoryTypes = _categoryTypeService.readAll();

        // Update budgetAmount & savedAmount for each CategoryType
        for(CategoryType type: categoryTypes) {
            double totalCategoryAmount = type.getBudgetAmount() - type.getSavedAmount();
            double newBudgetAmount = type.getBudgetAllocationPercentage() * incomeDto.getAmount();
            double newSavedAmount = newBudgetAmount - totalCategoryAmount;

            UpdateCategoryTypeDto categoryTypeDto = UpdateCategoryTypeDto.builder()
                            .savedAmount(newSavedAmount)
                            .budgetAllocationPercentage(type.getBudgetAllocationPercentage()) //budget allocation percentage remains the same
                            .amountAllocated(newBudgetAmount)
                            .build();

            _categoryTypeService.update(categoryTypeDto, type.getCategoryTypeId()); //update
        }

        incomeToUpdate.setUpdatedAt(LocalDateTime.now());
        incomeToUpdate.setAmount(incomeDto.getAmount());
        return incomeToUpdate;
    }

    //TODO: complete and add comments/logging
    @Override
    public void detete(Long incomeId) {

    }
}
