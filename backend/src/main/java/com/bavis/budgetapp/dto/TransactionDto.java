package com.bavis.budgetapp.dto;

import com.bavis.budgetapp.annotation.TransactionDtoValidAmount;
import com.bavis.budgetapp.annotation.TransactionDtoValidName;
import com.bavis.budgetapp.entity.Account;
import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.validator.group.TransactionDtoValidationGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO to represent a Transaction
 *
 * @author Kellen Bavis
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TransactionDtoValidName(groups = TransactionDtoValidationGroup.class)
@TransactionDtoValidAmount(groups = TransactionDtoValidationGroup.class)
public class TransactionDto {
    //Updated Properties
    private String updatedName;
    private double updatedAmount;

    //Original Properties
    private LocalDate date;
    private String logoUrl;
    private Account account;
    private Category category;
}
