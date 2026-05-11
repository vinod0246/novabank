package com.novabank.transactionservice.repository;

import com.novabank.transactionservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository 
        extends JpaRepository<Transaction, Long> {
    List<Transaction> findByFromAccountNumber(String accountNumber);
    List<Transaction> findByToAccountNumber(String accountNumber);
}