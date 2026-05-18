package com.novabank.accountservice.service;

import com.novabank.accountservice.model.Account;
import com.novabank.accountservice.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(1L);
        account.setAccountNumber("NB12345678");
        account.setOwnerName("John Doe");
        account.setEmail("john@example.com");
        account.setBalance(1000.0);
        account.setAccountType("SAVINGS");
    }

    @Test
    void createAccount_setsAccountNumberAndZeroBalance() {
        Account input = new Account();
        input.setOwnerName("Jane Doe");
        input.setEmail("jane@example.com");

        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        Account result = accountService.createAccount(input);

        assertTrue(result.getAccountNumber().startsWith("NB"));
        assertEquals(8, result.getAccountNumber().substring(2).length());
        assertEquals(0.0, result.getBalance());
        verify(accountRepository).save(input);
    }

    @Test
    void getAccountById_returnsAccount_whenFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        Account result = accountService.getAccountById(1L);

        assertEquals(1L, result.getId());
        assertEquals("NB12345678", result.getAccountNumber());
    }

    @Test
    void getAccountById_throwsException_whenNotFound() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> accountService.getAccountById(99L));

        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void getAccountByNumber_returnsAccount_whenFound() {
        when(accountRepository.findByAccountNumber("NB12345678"))
                .thenReturn(Optional.of(account));

        Account result = accountService.getAccountByNumber("NB12345678");

        assertEquals("NB12345678", result.getAccountNumber());
    }

    @Test
    void getAccountByNumber_throwsException_whenNotFound() {
        when(accountRepository.findByAccountNumber("NBUNKNOWN"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> accountService.getAccountByNumber("NBUNKNOWN"));

        assertTrue(ex.getMessage().contains("NBUNKNOWN"));
    }

    @Test
    void getAllAccounts_returnsAllAccounts() {
        Account second = new Account();
        second.setId(2L);
        second.setAccountNumber("NB87654321");

        when(accountRepository.findAll()).thenReturn(Arrays.asList(account, second));

        List<Account> result = accountService.getAllAccounts();

        assertEquals(2, result.size());
    }

    @Test
    void updateBalance_addsAmountToExistingBalance() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        Account result = accountService.updateBalance(1L, 500.0);

        assertEquals(1500.0, result.getBalance());
        verify(accountRepository).save(account);
    }

    @Test
    void depositMoney_addsAmountToBalance() {
        when(accountRepository.findByAccountNumber("NB12345678"))
                .thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        Account result = accountService.depositMoney("NB12345678", 200.0);

        assertEquals(1200.0, result.getBalance());
        verify(accountRepository).save(account);
    }

    @Test
    void deleteAccount_deletesSuccessfully_whenExists() {
        when(accountRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> accountService.deleteAccount(1L));
        verify(accountRepository).deleteById(1L);
    }

    @Test
    void deleteAccount_throwsException_whenNotFound() {
        when(accountRepository.existsById(99L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> accountService.deleteAccount(99L));

        assertTrue(ex.getMessage().contains("99"));
        verify(accountRepository, never()).deleteById(any());
    }
}
