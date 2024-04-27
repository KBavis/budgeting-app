package com.bavis.budgetapp.mapper;


import com.bavis.budgetapp.dto.AccountDTO;
import com.bavis.budgetapp.constants.AccountType;
import com.bavis.budgetapp.entity.Account;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.Assert.*;

@ContextConfiguration(classes = {AccountMapperImpl.class})
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
public class AccountMapperTests {

    @Autowired
    private AccountMapper accountMapper;


    @Test
    public void testToDto_Successful() {
        //Arrange
        Account source = Account.builder()
                .accountName("Test Account")
                .balance(1000.0)
                .accountType(AccountType.CHECKING)
                .build();
        AccountDTO target = new AccountDTO();

        //Act
        target = accountMapper.toDTO(source);

        //Assert
        assertEquals("Test Account", target.getAccountName());
        assertEquals(1000.0, target.getBalance(), .001);
        assertEquals(AccountType.CHECKING, target.getAccountType());
    }

}