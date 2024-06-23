package com.bavis.budgetapp.dto;

import com.bavis.budgetapp.annotation.AccountsDtoValidAccounts;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Kellen Bavis
 *
 * DTO utilized to store list of Account IDs needing to be synced
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@AccountsDtoValidAccounts
public class AccountsDto {
    private List<String> accounts;
}
