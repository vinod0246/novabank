package com.novabank.transactionservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novabank.transactionservice.model.Transaction;
import com.novabank.transactionservice.service
    .TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet
    .AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request
    .MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result
    .MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment =
    SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setFromAccountNumber("NB123");
        transaction.setToAccountNumber("NB456");
        transaction.setAmount(500.0);
        transaction.setStatus("SUCCESS");
        transaction.setDescription("Test transfer");
        transaction.setTransactionDate(
            LocalDateTime.now());
    }

    @Test
    @DisplayName("Test 1 - Health Check")
    void health_returnsOk() throws Exception {
        mockMvc.perform(get("/api/transactions/health"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                    "Transaction Service is running!"));
    }

    @Test
    @DisplayName("Test 2 - Get All Transactions")
    void getAllTransactions_returnsList() throws Exception {
        when(transactionService.getAllTransactions())
            .thenReturn(Arrays.asList(transaction));

        mockMvc.perform(
            get("/api/transactions/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()")
                    .value(1))
                .andExpect(jsonPath("$[0].status")
                    .value("SUCCESS"));
    }

    @Test
    @DisplayName("Test 3 - Transfer Money")
    void transfer_returnsTransaction() throws Exception {
        when(transactionService.transfer(
            anyString(), anyString(),
            anyDouble(), anyString()))
            .thenReturn(transaction);

        Map<String, Object> request = Map.of(
            "fromAccount", "NB123",
            "toAccount", "NB456",
            "amount", 500.0,
            "description", "Test transfer");

        mockMvc.perform(
            post("/api/transactions/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper
                    .writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                    .value("SUCCESS"))
                .andExpect(jsonPath("$.amount")
                    .value(500.0));
    }

    @Test
    @DisplayName("Test 4 - Delete Transaction")
    void deleteTransaction_returnsOk() throws Exception {
        doNothing().when(transactionService)
            .deleteTransaction(1L);

        mockMvc.perform(
            delete("/api/transactions/1"))
                .andExpect(status().isOk());

        verify(transactionService)
            .deleteTransaction(1L);
    }

    @Test
    @DisplayName("Test 5 - Get By Account Number")
    void getByAccount_returnsList() throws Exception {
        when(transactionService
            .getTransactionsByAccount("NB123"))
            .thenReturn(Arrays.asList(transaction));

        mockMvc.perform(
            get("/api/transactions/account/NB123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()")
                    .value(1));
    }
}