package com.novabank.transactionservice;

import com.novabank.transactionservice.model.Transaction;
import com.novabank.transactionservice.repository.TransactionRepository;
import com.novabank.transactionservice.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransactionServiceApplicationTests {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
    }

    @Test
    @DisplayName("Test 1 - Context Loads")
    void contextLoads() {
    }

    @Test
    @DisplayName("Test 2 - Get All Transactions Empty")
    void testGetAllTransactionsEmpty() {
        List<Transaction> transactions =
            transactionService.getAllTransactions();
        assertNotNull(transactions);
        assertEquals(0, transactions.size());
    }

    @Test
    @DisplayName("Test 3 - Delete Transaction")
    void testDeleteTransaction() {
        Transaction transaction = new Transaction();
        transaction.setFromAccountNumber("NB123");
        transaction.setToAccountNumber("NB456");
        transaction.setAmount(500.0);
        transaction.setStatus("SUCCESS");
        transaction.setDescription("Test");
        transaction.setTransactionDate(
            java.time.LocalDateTime.now());

        Transaction saved = 
            transactionRepository.save(transaction);
        transactionService.deleteTransaction(saved.getId());

        assertThrows(RuntimeException.class, () ->
            transactionRepository.findById(saved.getId())
                .orElseThrow(() -> 
                    new RuntimeException("Not found")));
    }

    @Test
    @DisplayName("Test 4 - Get Transactions By Account")
    void testGetTransactionsByAccount() {
        Transaction transaction = new Transaction();
        transaction.setFromAccountNumber("NB999");
        transaction.setToAccountNumber("NB888");
        transaction.setAmount(1000.0);
        transaction.setStatus("SUCCESS");
        transaction.setDescription("Test transfer");
        transaction.setTransactionDate(
            java.time.LocalDateTime.now());

        transactionRepository.save(transaction);

        List<Transaction> transactions =
            transactionService
                .getTransactionsByAccount("NB999");
        assertEquals(1, transactions.size());
        assertEquals("NB999",
            transactions.get(0).getFromAccountNumber());
    }
}