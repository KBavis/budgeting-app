package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.dao.IncomeRepository;
import com.bavis.budgetapp.dto.request.IncomeDto;
import com.bavis.budgetapp.dto.request.UpdateCategoryTypeDto;
import com.bavis.budgetapp.dto.request.UpdateIncomeDto;
import com.bavis.budgetapp.dto.response.IncomeResponseDto;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.entity.CategoryTypeVt;
import com.bavis.budgetapp.entity.Income;
import com.bavis.budgetapp.entity.IncomeVt;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.mapper.IncomeMapper;
import com.bavis.budgetapp.service.CategoryTypeService;
import com.bavis.budgetapp.service.EffectivityService;
import com.bavis.budgetapp.service.IncomeService;
import com.bavis.budgetapp.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
@Transactional
public class IncomeServiceImpl implements IncomeService {

    private final IncomeRepository _incomeRepository;
    private final UserService _userService;
    private final IncomeMapper _incomeMapper;
    private final CategoryTypeService _categoryTypeService;
    private final EffectivityService _effectivityService;

    public IncomeServiceImpl(IncomeRepository _incomeRepository, UserService _userService, IncomeMapper _incomeMapper,
            @Lazy CategoryTypeService _categoryTypeService, EffectivityService _effectivityService) {
        this._incomeRepository = _incomeRepository;
        this._userService = _userService;
        this._incomeMapper = _incomeMapper;
        this._categoryTypeService = _categoryTypeService;
        this._effectivityService = _effectivityService;
    }

    /**
     * Functionality to create and persist an Income entity
     *
     * @param incomeDto
     *                  - IncomeDto utilized to persist Income entity
     * @return
     *         - IncomeResponseDto
     */
    @Override
    public IncomeResponseDto create(IncomeDto incomeDto) {
        log.info("Creating Income: [{}]", incomeDto);

        User currentUser = _userService.getCurrentAuthUser();
        Income income = new Income();
        income.setUser(currentUser);
        income.setUpdatedAt(LocalDateTime.now());

        IncomeVt initialVt = IncomeVt.builder()
                .income(income)
                .incomeSource(incomeDto.getIncomeSource())
                .incomeType(incomeDto.getIncomeType())
                .amount(incomeDto.getAmount())
                .description(incomeDto.getDescription())
                .build();

        _effectivityService.applyVtUpdate(income.getValidTimes(), initialVt, LocalDate.now());

        log.info("Saving the following Income: [{}]", income);
        Income saved = _incomeRepository.save(income);
        IncomeVt activeVt = _effectivityService.getActiveVt(saved.getValidTimes(), LocalDate.now());
        return _incomeMapper.toResponseDto(saved, activeVt);
    }

    /**
     * Functionality to read all Income entities associated with Authenticated User
     * as of date
     *
     * @param asOf
     *             - Point-in-time date
     * @return
     *         - all incomes associated with Auth user as of date
     */
    @Override
    public List<Income> findAllEntities(LocalDate asOf) {
        log.info("Retrieving all Income entities corresponding to authenticated user asOf [{}]", asOf);
        User authenticatedUser = _userService.getCurrentAuthUser();
        return findAllEntitiesByUserId(authenticatedUser.getUserId(), asOf);
    }

    @Override
    public List<IncomeResponseDto> getAll(LocalDate asOf) {
        List<Income> incomes = findAllEntities(asOf);
        LocalDate target = (asOf != null) ? asOf : LocalDate.now();
        return incomes.stream()
                .map(inc -> {
                    IncomeVt activeVt = _effectivityService.getActiveVt(inc.getValidTimes(), target);
                    return _incomeMapper.toResponseDto(inc, activeVt);
                })
                .toList();
    }

    /**
     * Functionality to fetch an Income entity by ID as of date
     *
     * @param incomeId
     *                 - ID corresponding to Income entity to be fetched
     * @param asOf
     *                 - Point-in-time date
     * @return
     *         - Fetched Income entity corresponding to ID
     */
    @Override
    public Income findEntity(Long incomeId, LocalDate asOf) {
        log.info("Attempting to fetch income corresponding to ID {} asOf [{}]", incomeId, asOf);
        LocalDate target = (asOf != null) ? asOf : LocalDate.now();
        return _incomeRepository.findByIncomeIdAndAsOf(incomeId, target)
                .orElseThrow(() -> new RuntimeException("Unable to locate Income with the following ID: " + incomeId));
    }

    /**
     * Functionality to retrieve Income entities based on associated User ID as of
     * date
     *
     * @param userId
     *               - ID of User whom is related to a particular Income entity
     * @param asOf
     *               - Point-in-time date
     * @return
     *         - List of Income entities corresponding to particular user
     */
    @Override
    public List<Income> findAllEntitiesByUserId(Long userId, LocalDate asOf) {
        log.info("Attempting to find Income[s] for User with ID {} asOf [{}]", userId, asOf);
        LocalDate target = (asOf != null) ? asOf : LocalDate.now();
        return _incomeRepository.findByUserUserIdAndAsOf(userId, target);
    }

    /**
     * Functionality to find the sum of all Income entities pertaining to a
     * particular user as of date
     *
     * @param userId
     *               - User ID to fetch Income entities for
     * @param asOf
     *               - Point-in-time date
     * @return
     *         - Total amount of each of the users Income's combined as of date
     */
    @Override
    public double findUserTotalIncomeAmount(Long userId, LocalDate asOf) {
        log.info("Calculating total User monthly income for User with ID {} asOf [{}]", userId, asOf);
        return findAllEntitiesByUserId(userId, asOf).stream()
                .map(income -> {
                    IncomeVt active = _effectivityService.getActiveVt(income.getValidTimes(), asOf);
                    return active.getAmount();
                })
                .reduce(0.0, Double::sum);
    }

    /**
     * Functionality to update a particular Income entity with updated attributes
     *
     * @param incomeDto
     *                  - updated income amount & corresponding Income ID to update
     * @return
     *         - Updated IncomeResponseDto
     */
    @Override
    public IncomeResponseDto update(UpdateIncomeDto incomeDto) {
        log.info("Attempting to update user's Income via the following incomeDto: [{}]", incomeDto);
        User authUser = _userService.getCurrentAuthUser();

        Income incomeToUpdate = findEntity(incomeDto.getIncomeId(), null);

        if(!Objects.equals(incomeToUpdate.getUser().getUserId(), authUser.getUserId())){
            log.error("Error: Non-auth user is attempting to update income not owned by them");
            throw new RuntimeException("Unable to update user Income due to user not being owner of specified income");
        }

        incomeToUpdate.setUpdatedAt(LocalDateTime.now());
        IncomeVt active = _effectivityService.getActiveVt(incomeToUpdate.getValidTimes(), LocalDate.now());

        IncomeVt updateVt = IncomeVt.builder()
                .income(incomeToUpdate)
                .incomeSource(active.getIncomeSource())
                .incomeType(active.getIncomeType())
                .amount(incomeDto.getAmount())
                .description(active.getDescription())
                .build();

        _effectivityService.applyVtUpdate(incomeToUpdate.getValidTimes(), updateVt, LocalDate.now());
        Income saved = _incomeRepository.saveAndFlush(incomeToUpdate);

        List<CategoryType> categoryTypes = _categoryTypeService.findAllEntities(null);

        for (CategoryType type : categoryTypes) {
            CategoryTypeVt ctActive = _effectivityService.getActiveVt(type.getValidTimes(), LocalDate.now());
            double currentAllocPct = ctActive.getBudgetAllocationPercentage();

            UpdateCategoryTypeDto categoryTypeDto = UpdateCategoryTypeDto.builder()
                    .budgetAllocationPercentage(currentAllocPct)
                    .build();

            _categoryTypeService.update(categoryTypeDto, type.getCategoryTypeId());
        }

        IncomeVt activeVt = _effectivityService.getActiveVt(saved.getValidTimes(), LocalDate.now());
        return _incomeMapper.toResponseDto(saved, activeVt);
    }

    /**
     * Functionality to delete a particular Income entity
     *
     * @param incomeId
     *                 - ID corresponding to Income entity to be deleted
     */
    @Override
    public void delete(Long incomeId) {
        log.info("Attempting to delete Income entity with ID {}", incomeId);
        User authUser = _userService.getCurrentAuthUser();
        Income incomeToDelete = findEntity(incomeId, null);

        if (!Objects.equals(incomeToDelete.getUser().getUserId(), authUser.getUserId())) {
            log.error("Error: Non-auth user is attempting to delete income not owned by them");
            throw new RuntimeException("Unable to delete Income due to user not being owner of specified income");
        }

        incomeToDelete.setEndDate(LocalDate.now().minusDays(1));
        _incomeRepository.save(incomeToDelete);
        _incomeRepository.flush();
        log.info("Successfully soft-deleted Income entity with ID {}", incomeId);
    }
}
