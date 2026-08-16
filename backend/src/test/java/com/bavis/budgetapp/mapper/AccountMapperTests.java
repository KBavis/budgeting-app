package com.bavis.budgetapp.mapper;

import com.bavis.budgetapp.dto.response.AccountResponseDto;
import com.bavis.budgetapp.constants.AccountType;
import com.bavis.budgetapp.entity.Account;
import com.bavis.budgetapp.entity.AccountVt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;

import static org.junit.Assert.*;

@ContextConfiguration(classes = {AccountMapperImpl.class})
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
public class AccountMapperTests {

    @Autowired
    private AccountMapper accountMapper;

    @Test
    public void testToResponseDto_Successful() {
        // Arrange
        Account account = Account.builder()
                .accountId("account-id-xyz")
                .validTimes(new ArrayList<>())
                .build();

        AccountVt activeVt = AccountVt.builder()
                .account(account)
                .accountName("Test Account")
                .balance(1000.0)
                .accountType(AccountType.CHECKING)
                .build();

        // Act
        AccountResponseDto target = accountMapper.toResponseDto(account, activeVt);

        // Assert
        assertEquals("Test Account", target.getAccountName());
        assertEquals(1000.0, target.getBalance(), .001);
        assertEquals(AccountType.CHECKING, target.getAccountType());
        assertEquals(account.getAccountId(), target.getAccountId());
    }
}