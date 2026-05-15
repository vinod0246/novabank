package com.novabank.accountservice.controller;

import com.novabank.accountservice.model.Account;
import com.novabank.accountservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/health")
    public String health() {
        return "Account Service is running!";
    }

    @PostMapping("/create")
    public ResponseEntity<Account> createAccount(
            @RequestBody Account account) {
        return ResponseEntity.ok(
            accountService.createAccount(account));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccount(
            @PathVariable Long id) {
        return ResponseEntity.ok(
            accountService.getAccountById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(
            accountService.getAllAccounts());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/deposit")
    public ResponseEntity<Account> depositMoney(
            @RequestBody Map<String, Object> request) {
        String accountNumber = (String) request.get("accountNumber");
        Double amount = Double.valueOf(
            request.get("amount").toString());
        return ResponseEntity.ok(
            accountService.depositMoney(accountNumber, amount));
    }

    @PutMapping("/balance/{accountNumber}")
    public ResponseEntity<Account> updateBalanceByAccountNumber(
            @PathVariable String accountNumber,
            @RequestParam Double amount) {
        Account account = accountService
            .getAccountByNumber(accountNumber);
        return ResponseEntity.ok(
            accountService.updateBalance(account.getId(), amount));
    }
}