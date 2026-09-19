package com.bavis.budgetapp.mapper;

import com.bavis.budgetapp.constants.ConnectionStatus;
import com.bavis.budgetapp.dto.response.AccountResponseDto;
import com.bavis.budgetapp.constants.AccountType;
import com.bavis.budgetapp.entity.Account;
import com.bavis.budgetapp.entity.AccountVt;
import com.bavis.budgetapp.entity.Connection;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    public void testToResponseDto_ConnectionRequiringLogin_FlagsRequiresReauth() {
        Connection connection = Connection.builder()
                .connectionStatus(ConnectionStatus.DISCONNECTED)
                .errorCode("ITEM_LOGIN_REQUIRED")
                .build();
        Account account = Account.builder().accountId("id").connection(connection).validTimes(new ArrayList<>()).build();
        AccountVt activeVt = AccountVt.builder().account(account).accountName("Discover").balance(1.0).accountType(AccountType.CREDIT).build();

        AccountResponseDto dto = accountMapper.toResponseDto(account, activeVt);

        assertTrue(dto.isRequiresReauth());
        assertEquals(ConnectionStatus.DISCONNECTED, dto.getConnectionStatus());
        assertEquals("ITEM_LOGIN_REQUIRED", dto.getConnectionErrorCode());
    }

    @Test
    public void testToResponseDto_HealthyConnection_DoesNotRequireReauth() {
        Connection connection = Connection.builder().connectionStatus(ConnectionStatus.CONNECTED).build();
        Account account = Account.builder().accountId("id").connection(connection).validTimes(new ArrayList<>()).build();
        AccountVt activeVt = AccountVt.builder().account(account).accountName("Chase").balance(1.0).accountType(AccountType.CHECKING).build();

        AccountResponseDto dto = accountMapper.toResponseDto(account, activeVt);

        assertFalse(dto.isRequiresReauth());
        assertEquals(ConnectionStatus.CONNECTED, dto.getConnectionStatus());
        assertNull(dto.getConnectionErrorCode());
    }

    @Test
    public void testToResponseDto_NoConnection_DoesNotRequireReauth() {
        Account account = Account.builder().accountId("id").validTimes(new ArrayList<>()).build();
        AccountVt activeVt = AccountVt.builder().account(account).accountName("Manual").balance(1.0).accountType(AccountType.CHECKING).build();

        AccountResponseDto dto = accountMapper.toResponseDto(account, activeVt);

        assertFalse(dto.isRequiresReauth());
        assertNull(dto.getConnectionStatus());
    }
}
