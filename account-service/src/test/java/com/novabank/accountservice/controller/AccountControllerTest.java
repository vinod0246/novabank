package com.novabank.accountservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novabank.accountservice.model.Account;
import com.novabank.accountservice.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = 
    SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(1L);
        account.setAccountNumber("NB12345678");
        account.setOwnerName("Vinod Kumar");
        account.setEmail("vinod@novabank.com");
        account.setBalance(1000.0);
        account.setAccountType("SAVINGS");
    }

    @Test
    @DisplayName("Test 1 - Health Check")
    void health_returnsOk() throws Exception {
        mockMvc.perform(get("/api/accounts/health"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                    "Account Service is running!"));
    }

    @Test
    @DisplayName("Test 2 - Create Account")
    void createAccount_returnsCreatedAccount() throws Exception {
        when(accountService.createAccount(
            any(Account.class))).thenReturn(account);

        mockMvc.perform(post("/api/accounts/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber")
                    .value("NB12345678"))
                .andExpect(jsonPath("$.ownerName")
                    .value("Vinod Kumar"));
    }

    @Test
    @DisplayName("Test 3 - Get Account By ID")
    void getAccount_returnsAccount() throws Exception {
        when(accountService.getAccountById(1L))
            .thenReturn(account);

        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accountNumber")
                    .value("NB12345678"));
    }

    @Test
    @DisplayName("Test 4 - Get All Accounts")
    void getAllAccounts_returnsList() throws Exception {
        Account second = new Account();
        second.setId(2L);
        second.setAccountNumber("NB87654321");
        second.setOwnerName("Raja Kumar");

        when(accountService.getAllAccounts())
            .thenReturn(Arrays.asList(account, second));

        mockMvc.perform(get("/api/accounts/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Test 5 - Delete Account")
    void deleteAccount_returnsNoContent() throws Exception {
        doNothing().when(accountService).deleteAccount(1L);

        mockMvc.perform(delete("/api/accounts/1"))
                .andExpect(status().isNoContent());

        verify(accountService).deleteAccount(1L);
    }

    @Test
    @DisplayName("Test 6 - Deposit Money")
    void depositMoney_returnsUpdatedAccount() throws Exception {
        account.setBalance(1200.0);
        when(accountService.depositMoney(
            eq("NB12345678"), eq(200.0))).thenReturn(account);

        Map<String, Object> request = Map.of(
            "accountNumber", "NB12345678",
            "amount", 200.0);

        mockMvc.perform(post("/api/accounts/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper
                    .writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance")
                    .value(1200.0));
    }

    @Test
    @DisplayName("Test 7 - Update Balance")
    void updateBalance_returnsUpdatedAccount() throws Exception {
        account.setBalance(1500.0);
        when(accountService.getAccountByNumber("NB12345678"))
            .thenReturn(account);
        when(accountService.updateBalance(eq(1L), eq(500.0)))
            .thenReturn(account);

        mockMvc.perform(put(
                "/api/accounts/balance/NB12345678")
                .param("amount", "500.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance")
                    .value(1500.0));
    }
}