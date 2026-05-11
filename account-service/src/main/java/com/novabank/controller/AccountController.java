package com.novabank.accountservice.controller;

import com.novabank.accountservice.model.Account;
import com.novabank.accountservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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

    @PutMapping("/balance/{accountNumber}")
    public ResponseEntity<Account> updateBalanceByAccountNumber(
            @PathVariable String accountNumber,
            @RequestParam Double amount) {
                Account account =accountService.getAccountByNumber(accountNumber);
                return ResponseEntity.ok(
                    accountService.updateBalance(account.getId(), amount))
        ;
    }
    

}