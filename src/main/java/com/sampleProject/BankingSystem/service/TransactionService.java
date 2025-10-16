package com.sampleProject.BankingSystem.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sampleProject.BankingSystem.model.Account;
import com.sampleProject.BankingSystem.model.BankTransaction;
import com.sampleProject.BankingSystem.repository.BankTransactionRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {
    private final BankTransactionRepository transactionRepository;

    public TransactionService(BankTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public BankTransaction record(Account account, BigDecimal amount, String type, String note) {
        BankTransaction txn = new BankTransaction();
        txn.setAccount(account);
        txn.setAmount(amount);
        txn.setTxnType(type);
        txn.setNote(note);
        return transactionRepository.save(txn);
    }

    public List<com.sampleProject.BankingSystem.model.BankTransaction> list(Long accountId) {
        return transactionRepository.findByAccount_IdOrderByTxnDateDesc(accountId);
    }
}

