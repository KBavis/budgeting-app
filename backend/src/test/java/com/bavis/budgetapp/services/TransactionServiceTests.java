package com.bavis.budgetapp.services;

import com.bavis.budgetapp.dao.TransactionRepository;
import com.bavis.budgetapp.dto.PlaidTransactionDto;
import com.bavis.budgetapp.dto.PlaidTransactionSyncResponseDto;
import com.bavis.budgetapp.dto.TransactionSyncRequestDto;
import com.bavis.budgetapp.entity.Account;
import com.bavis.budgetapp.entity.Connection;
import com.bavis.budgetapp.entity.Transaction;
import com.bavis.budgetapp.mapper.TransactionMapper;
import com.bavis.budgetapp.service.impl.AccountServiceImpl;
import com.bavis.budgetapp.service.impl.ConnectionServiceImpl;
import com.bavis.budgetapp.service.impl.PlaidServiceImpl;
import com.bavis.budgetapp.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "test")
public class TransactionServiceTests {

    @Mock
    private PlaidServiceImpl plaidService;

    @Mock
    AccountServiceImpl accountService;

    @Mock
    private ConnectionServiceImpl connectionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;


    @Test
    void testSyncTransactions_Successful() {
        //Arrange
        //TODO: move this arrange logic to test helper or setup method
        String nextCursor = "next-cursor";
        String previousCursor = "previous-cursor";
        LocalDate date = LocalDate.now();
        String accountId = "account-id";
        String accessToken = "access-token";

        PlaidTransactionDto.CounterpartyDto counterpartyDto = PlaidTransactionDto.CounterpartyDto.builder()
                .logo_url("logo_url")
                .name("Chase")
                .build();

        PlaidTransactionDto.PersonalFinanceCategoryDto personalFinanceCategoryDto = PlaidTransactionDto.PersonalFinanceCategoryDto.builder()
                .primary("PRIMARY")
                .detailed("DETAILED")
                .confidence_level("HIGH")
                .build();

        PlaidTransactionDto plaidTransactionDtoOne = PlaidTransactionDto.builder()
                .transaction_id("12345")
                .counterpartyDto(counterpartyDto)
                .personalFinanceCategoryDto(personalFinanceCategoryDto)
                .amount(1000.0)
                .datetime(date)
                .account_id(accountId)
                .build();

        PlaidTransactionDto plaidTransactionDtoTwo = PlaidTransactionDto.builder()
                .transaction_id("6789")
                .counterpartyDto(counterpartyDto)
                .personalFinanceCategoryDto(personalFinanceCategoryDto)
                .amount(2000.0)
                .datetime(date)
                .account_id(accountId)
                .build();


        PlaidTransactionDto plaidTransactionDtoThree = PlaidTransactionDto.builder()
                .transaction_id("6789")
                .counterpartyDto(counterpartyDto)
                .personalFinanceCategoryDto(personalFinanceCategoryDto)
                .amount(3000.0)
                .datetime(date)
                .account_id(accountId)
                .build();

        List<PlaidTransactionDto> addedTransactions = List.of(plaidTransactionDtoOne);
        List<PlaidTransactionDto> removedTransactions = List.of(plaidTransactionDtoTwo);
        List<PlaidTransactionDto> modifiedTransactions = List.of(plaidTransactionDtoThree);

        PlaidTransactionSyncResponseDto syncResponseDto = PlaidTransactionSyncResponseDto.builder()
                .added(addedTransactions)
                .modified(modifiedTransactions)
                .removed(removedTransactions)
                .next_cursor(nextCursor)
                .build();


        String accountIdOne = "12345XYZ";
        String accountIdTwo = "6789ABCD";

        Connection accountConnectionOne = Connection.builder()
                .connectionId(5L)
                .accessToken(accessToken)
                .previousCursor(previousCursor)
                .build();

        Connection accountConnectionTwo = Connection.builder()
                .connectionId(10L)
                .accessToken(accessToken)
                .previousCursor(previousCursor)
                .build();

        Account accountOne = Account.builder()
                .accountId(accountIdOne)
                .connection(accountConnectionOne)
                .build();

        Account accountTwo = Account.builder()
                .accountId(accountIdTwo)
                .connection(accountConnectionTwo)
                .build();
        ArrayList<String> accountIds = new ArrayList<>(List.of(accountIdOne, accountIdTwo));
        TransactionSyncRequestDto transactionSyncRequestDto = TransactionSyncRequestDto.builder()
                .accounts(accountIds)
                .build();


        //Mock
        when(accountService.read(accountIdOne)).thenReturn(accountOne);
        when(accountService.read(accountIdTwo)).thenReturn(accountTwo);
        when(plaidService.syncTransactions(accessToken, previousCursor)).thenReturn(syncResponseDto);
        when(transactionMapper.toEntity(any(PlaidTransactionDto.class))).thenAnswer(invocationOnMock -> {
            PlaidTransactionDto dto = invocationOnMock.getArgument(0);
            return Transaction.builder()
                    .transactionId(dto.getTransaction_id())
                    .name(dto.getCounterpartyDto().getName())
                    .amount(dto.getAmount())
                    .date(dto.getDatetime())
                    .account(null)  //mapper shouldn't map Account or Category entities
                    .category(null)
                    .build();
        });
        when(transactionRepository.saveAllAndFlush(anyList())).thenAnswer(invocationOnMock ->{
            return invocationOnMock.<List<Transaction>>getArgument(0);
        });
        doNothing().when(transactionRepository).deleteAll(anyList());
        when(connectionService.update(any(Connection.class), any(Long.class))).thenAnswer(invocationOnMock -> {
            Connection connectionToUpdate = invocationOnMock.getArgument(0);
            connectionToUpdate.setPreviousCursor(previousCursor);
            return connectionToUpdate;
        });

        //Act
        List<Transaction> actualTransactions = transactionService.syncTransactions(transactionSyncRequestDto);

       //Assert
        assertNotNull(actualTransactions);
        assertEquals(4, actualTransactions.size()); //2 modified, 2 added

       //Verfiy
        verify(accountService, times(1)).read(accountIdOne);
        verify(accountService, times(1)).read(accountIdTwo);
        verify(connectionService, times(1)).update(accountConnectionOne, accountConnectionOne.getConnectionId());
        verify(connectionService, times(1)).update(accountConnectionTwo, accountConnectionTwo.getConnectionId());
        verify(transactionRepository, times(2)).saveAllAndFlush(anyList());
        verify(transactionRepository, times(2)).deleteAll(anyList());
        verify(transactionMapper, times(6)).toEntity(any(PlaidTransactionDto.class));
    }
}