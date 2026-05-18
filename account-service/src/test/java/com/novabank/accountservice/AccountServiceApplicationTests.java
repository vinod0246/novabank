package com.novabank.accountservice;

import com.novabank.accountservice.model.Account;
import com.novabank.accountservice.repository.AccountRepository;
import com.novabank.accountservice.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccountServiceApplicationTests {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("Test 1 - Create Account Successfully")
    void testCreateAccount() {
        Account account = new Account();
        account.setOwnerName("Vinod Kumar");
        account.setEmail("vinod@novabank.com");
        account.setAccountType("SAVINGS");

        Account saved = accountService.createAccount(account);

        assertNotNull(saved.getId());
        assertNotNull(saved.getAccountNumber());
        assertTrue(saved.getAccountNumber().startsWith("NB"));
        assertEquals("Vinod Kumar", saved.getOwnerName());
        assertEquals(0.0, saved.getBalance());
    }

    @Test
    @DisplayName("Test 2 - Get Account By ID")
    void testGetAccountById() {
        Account account = new Account();
        account.setOwnerName("Raja Kumar");
        account.setEmail("raja@novabank.com");
        account.setAccountType("CURRENT");

        Account saved = accountService.createAccount(account);
        Account found = accountService.getAccountById(saved.getId());

        assertEquals(saved.getId(), found.getId());
        assertEquals("Raja Kumar", found.getOwnerName());
    }

    @Test
    @DisplayName("Test 3 - Deposit Money")
    void testDepositMoney() {
        Account account = new Account();
        account.setOwnerName("Test User");
        account.setEmail("test@novabank.com");
        account.setAccountType("SAVINGS");

        Account saved = accountService.createAccount(account);
        Account afterDeposit = accountService
            .depositMoney(saved.getAccountNumber(), 1000.0);

        assertEquals(1000.0, afterDeposit.getBalance());
    }

    @Test
    @DisplayName("Test 4 - Get All Accounts")
    void testGetAllAccounts() {
        Account account1 = new Account();
        account1.setOwnerName("User One");
        account1.setEmail("one@novabank.com");
        account1.setAccountType("SAVINGS");
        accountService.createAccount(account1);

        Account account2 = new Account();
        account2.setOwnerName("User Two");
        account2.setEmail("two@novabank.com");
        account2.setAccountType("CURRENT");
        accountService.createAccount(account2);

        assertEquals(2, accountService.getAllAccounts().size());
    }

    @Test
    @DisplayName("Test 5 - Delete Account")
    void testDeleteAccount() {
        Account account = new Account();
        account.setOwnerName("Delete Test");
        account.setEmail("delete@novabank.com");
        account.setAccountType("SAVINGS");

        Account saved = accountService.createAccount(account);
        accountService.deleteAccount(saved.getId());

        assertThrows(RuntimeException.class, () ->
            accountService.getAccountById(saved.getId()));
    }

    @Test
    @DisplayName("Test 6 - Account Not Found Exception")
    void testAccountNotFound() {
        assertThrows(RuntimeException.class, () ->
            accountService.getAccountById(999L));
    }

    @Test
    @DisplayName("Test 7 - Update Balance")
    void testUpdateBalance() {
        Account account = new Account();
        account.setOwnerName("Balance Test");
        account.setEmail("balance@novabank.com");
        account.setAccountType("SAVINGS");

        Account saved = accountService.createAccount(account);
        accountService.depositMoney(
            saved.getAccountNumber(), 2000.0);
        Account updated = accountService
            .updateBalance(saved.getId(), 500.0);

        assertEquals(2500.0, updated.getBalance());
    }
}