package com.sampleProject.BankingSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sampleProject.BankingSystem.model.BankTransaction;

import java.util.List;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
    List<BankTransaction> findByAccount_IdOrderByTxnDateDesc(Long accountId);
}
