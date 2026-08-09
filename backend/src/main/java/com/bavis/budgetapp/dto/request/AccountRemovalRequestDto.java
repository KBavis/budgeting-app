package com.bavis.budgetapp.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Kellen Bavis
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountRemovalRequestDto {

    @NotEmpty(message = "accessToken must not be empty")
    private String accessToken;
}
