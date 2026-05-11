package com.novabank.transactionservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fromAccountNumber;
    private String toAccountNumber;
    private Double amount;
    private String status; // SUCCESS or FAILED
    private LocalDateTime transactionDate;
    private String description;

    // Getters
    public Long getId() { return id; }
    public String getFromAccountNumber() { return fromAccountNumber; }
    public String getToAccountNumber() { return toAccountNumber; }
    public Double getAmount() { return amount; }
    public String getStatus() { return status; }
    public LocalDateTime getTransactionDate() { return transactionDate; }
    public String getDescription() { return description; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setFromAccountNumber(String fromAccountNumber) {
        this.fromAccountNumber = fromAccountNumber; }
    public void setToAccountNumber(String toAccountNumber) {
        this.toAccountNumber = toAccountNumber; }
    public void setAmount(Double amount) { this.amount = amount; }
    public void setStatus(String status) { this.status = status; }
    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate; }
    public void setDescription(String description) {
        this.description = description; }
}