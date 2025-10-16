package com.sampleProject.BankingSystem.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sampleProject.BankingSystem.dto.AccountDtos;
import com.sampleProject.BankingSystem.model.*;
import com.sampleProject.BankingSystem.repository.AccountRepository;
import com.sampleProject.BankingSystem.repository.CustomerRepository;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountService {
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionService transactionService;
    private final SecureRandom random = new SecureRandom();

    public AccountService(CustomerRepository customerRepository, AccountRepository accountRepository, TransactionService transactionService) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.transactionService = transactionService;
    }

    @Transactional
    public Account createAccount(AccountDtos.CreateAccountRequest req) {
        if (req.customerId == null) throw new IllegalArgumentException("customerId required");
        Customer customer = customerRepository.findById(req.customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + req.customerId));

        Account account = new Account();
        account.setCustomer(customer);
        account.setAccountType(req.accountType);
        account.setInterestRate(req.interestRate);
        account.setOverdraftLimit(req.overdraftLimit);
        account.setAccountNumber(req.accountNumber != null ? req.accountNumber : generateAccountNumber());
        BigDecimal opening = req.openingBalance != null ? req.openingBalance : BigDecimal.ZERO;
        if (opening.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("openingBalance must be >= 0");
        account.setBalance(opening);
        account = accountRepository.save(account);
        if (opening.compareTo(BigDecimal.ZERO) > 0) {
            transactionService.record(account, opening, "OPENING_DEPOSIT", "Opening balance");
        }
        return account;
    }

    public Account getAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + id));
    }

    @Transactional
    public Account deposit(Long id, BigDecimal amount, String note) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        Account acc = accountRepository.findWithLockingById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + id));
        acc.setBalance(acc.getBalance().add(amount));
        accountRepository.save(acc);
        transactionService.record(acc, amount, "DEPOSIT", note);
        return acc;
    }

    @Transactional
    public Account withdraw(Long id, BigDecimal amount, String note) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        Account acc = accountRepository.findWithLockingById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + id));
        BigDecimal available = acc.getBalance();
        if (acc.getAccountType() == AccountType.CURRENT && acc.getOverdraftLimit() != null) {
            available = available.add(acc.getOverdraftLimit());
        }
        if (available.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
        acc.setBalance(acc.getBalance().subtract(amount));
        accountRepository.save(acc);
        transactionService.record(acc, amount, "WITHDRAWAL", note);
        return acc;
    }

    @Transactional
    public void transfer(Long fromId, Long toId, BigDecimal amount, String note) {
        if (fromId == null || toId == null) throw new IllegalArgumentException("Both fromAccountId and toAccountId are required");
        if (fromId.equals(toId)) throw new IllegalArgumentException("fromAccountId and toAccountId must be different");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Amount must be positive");
        // lock ordering by id to avoid deadlock
        Long first = fromId < toId ? fromId : toId;
        Long second = fromId < toId ? toId : fromId;
        Account firstAcc = accountRepository.findWithLockingById(first)
                .orElseThrow(() -> new IllegalArgumentException("Account not found (ensure you are using ACCOUNT IDs, not customer IDs): " + first));
        Account secondAcc = accountRepository.findWithLockingById(second)
                .orElseThrow(() -> new IllegalArgumentException("Account not found (ensure you are using ACCOUNT IDs, not customer IDs): " + second));
        Account from = fromId.equals(firstAcc.getId()) ? firstAcc : secondAcc;
        Account to = from == firstAcc ? secondAcc : firstAcc;

        BigDecimal available = from.getBalance();
        if (from.getAccountType() == AccountType.CURRENT && from.getOverdraftLimit() != null) {
            available = available.add(from.getOverdraftLimit());
        }
        if (available.compareTo(amount) < 0) throw new IllegalStateException("Insufficient funds");
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        accountRepository.save(from);
        accountRepository.save(to);
        transactionService.record(from, amount, "TRANSFER_OUT", note);
        transactionService.record(to, amount, "TRANSFER_IN", note);
    }

    public List<BankTransaction> listTransactions(Long accountId) {
        return transactionService.list(accountId);
    }

    public List<Account> listAccountsForCustomer(Long customerId) {
        return accountRepository.findByCustomer_Id(customerId);
    }

    private String generateAccountNumber() {
        // simple 12-digit random
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) sb.append(random.nextInt(10));
        // ensure uniqueness attempt (not strict, just quick check)
        Optional<Account> existing = accountRepository.findByAccountNumber(sb.toString());
        return existing.isPresent() ? generateAccountNumber() : sb.toString();
    }

    @Transactional
    public void deleteAccount(Long id) {
        Account acc = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + id));
        if (acc.getBalance() != null && acc.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Account balance must be zero to delete");
        }
        // JPA cascade (orphanRemoval) will delete transactions
        accountRepository.delete(acc);
    }
}
