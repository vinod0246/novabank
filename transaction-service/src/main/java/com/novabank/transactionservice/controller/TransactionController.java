package com.novabank.transactionservice.controller;

import com.novabank.transactionservice.model.Transaction;
import com.novabank.transactionservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // Health check
    @GetMapping("/health")
    public String health() {
        return "Transaction Service is running!";
    }

    // Transfer money
    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transfer(
            @RequestBody Map<String, Object> request) {
        String fromAccount = (String) request.get("fromAccount");
        String toAccount = (String) request.get("toAccount");
        Double amount = Double.valueOf(
            request.get("amount").toString());
        String description = (String) request.get("description");

        Transaction transaction = transactionService.transfer(
            fromAccount, toAccount, amount, description);
        return ResponseEntity.ok(transaction);
    }

    // Get all transactions
    @GetMapping("/all")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(
            transactionService.getAllTransactions());
    }

    // Get transactions by account
    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<Transaction>> getByAccount(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(
            transactionService
                .getTransactionsByAccount(accountNumber));
    }

   // Delete transaction
@DeleteMapping("/{id}")
public ResponseEntity<String> deleteTransaction(
        @PathVariable Long id) {
    transactionService.deleteTransaction(id);
    return ResponseEntity.ok(
        "Transaction " + id + " deleted successfully!");
}
}