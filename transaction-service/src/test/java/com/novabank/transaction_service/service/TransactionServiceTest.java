package com.novabank.transactionservice.service;

import com.novabank.transactionservice.model.Transaction;
import com.novabank.transactionservice.repository
    .TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    @DisplayName("Test 1 - Get All Transactions")
    void testGetAllTransactions() {
        Transaction t1 = new Transaction();
        t1.setId(1L);
        t1.setFromAccountNumber("NB123");
        t1.setToAccountNumber("NB456");
        t1.setAmount(500.0);
        t1.setStatus("SUCCESS");

        Transaction t2 = new Transaction();
        t2.setId(2L);
        t2.setFromAccountNumber("NB789");
        t2.setToAccountNumber("NB012");
        t2.setAmount(1000.0);
        t2.setStatus("SUCCESS");

        when(transactionRepository.findAll())
            .thenReturn(Arrays.asList(t1, t2));

        List<Transaction> result =
            transactionService.getAllTransactions();

        assertEquals(2, result.size());
        verify(transactionRepository).findAll();
    }

    @Test
    @DisplayName("Test 2 - Get Transactions By Account")
    void testGetTransactionsByAccount() {
        Transaction t1 = new Transaction();
        t1.setFromAccountNumber("NB123");
        t1.setAmount(500.0);

        when(transactionRepository
            .findByFromAccountNumber("NB123"))
            .thenReturn(Arrays.asList(t1));

        List<Transaction> result =
            transactionService
                .getTransactionsByAccount("NB123");

        assertEquals(1, result.size());
        assertEquals("NB123",
            result.get(0).getFromAccountNumber());
    }

    @Test
    @DisplayName("Test 3 - Delete Transaction Success")
    void testDeleteTransactionSuccess() {
        when(transactionRepository.existsById(1L))
            .thenReturn(true);
        doNothing().when(transactionRepository)
            .deleteById(1L);

        transactionService.deleteTransaction(1L);

        verify(transactionRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Test 4 - Delete Transaction Not Found")
    void testDeleteTransactionNotFound() {
        when(transactionRepository.existsById(999L))
            .thenReturn(false);

        assertThrows(RuntimeException.class, () ->
            transactionService.deleteTransaction(999L));
    }

    @Test
    @DisplayName("Test 5 - Transfer Failed")
    void testTransferFailed() {
        when(transactionRepository.save(
            any(Transaction.class)))
            .thenAnswer(i -> i.getArgument(0));

        Transaction result = transactionService.transfer(
            "INVALID", "INVALID", 500.0, "Test");

        assertEquals("FAILED", result.getStatus());
    }
}