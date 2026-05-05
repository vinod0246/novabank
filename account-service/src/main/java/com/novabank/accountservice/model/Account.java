package com.novabank.accountservice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountNumber;
    private String ownerName;
    private String email;
    private Double balance;
    private String accountType;

    // Getters
    public Long getId() { return id; }
    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    public String getEmail() { return email; }
    public Double getBalance() { return balance; }
    public String getAccountType() { return accountType; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setAccountNumber(String accountNumber) { 
        this.accountNumber = accountNumber; 
    }
    public void setOwnerName(String ownerName) { 
        this.ownerName = ownerName; 
    }
    public void setEmail(String email) { 
        this.email = email; 
    }
    public void setBalance(Double balance) { 
        this.balance = balance; 
    }
    public void setAccountType(String accountType) { 
        this.accountType = accountType; 
    }
}