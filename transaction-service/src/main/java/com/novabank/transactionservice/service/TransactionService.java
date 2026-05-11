package com.novabank.transactionservice.service;

import com.novabank.transactionservice.model.Transaction;
import com.novabank.transactionservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    // RestTemplate is used to call Account Service
    private RestTemplate restTemplate = new RestTemplate();

    private final String ACCOUNT_SERVICE_URL = 
        "http://localhost:8082/api/accounts";

    // Transfer money between accounts
    public Transaction transfer(String fromAccount, 
            String toAccount, Double amount, String description) {
        
        Transaction transaction = new Transaction();
        transaction.setFromAccountNumber(fromAccount);
        transaction.setToAccountNumber(toAccount);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setTransactionDate(LocalDateTime.now());

        try {
            // Debit from sender account (subtract amount)
            restTemplate.put(
                ACCOUNT_SERVICE_URL + "/balance/" + fromAccount 
                + "?amount=" + (-amount), null);

            // Credit to receiver account (add amount)
            restTemplate.put(
                ACCOUNT_SERVICE_URL + "/balance/" + toAccount 
                + "?amount=" + amount, null);

            transaction.setStatus("SUCCESS");
        } catch (Exception e) {
            transaction.setStatus("FAILED");
        }

        return transactionRepository.save(transaction);
    }

    // Get all transactions
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // Get transactions by account number
    public List<Transaction> getTransactionsByAccount(
            String accountNumber) {
        return transactionRepository
            .findByFromAccountNumber(accountNumber);
    }

    //Delete  transaction
    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException(
                "Transaction not found with id: " + id);
        }
        transactionRepository.deleteById(id);
    }
}