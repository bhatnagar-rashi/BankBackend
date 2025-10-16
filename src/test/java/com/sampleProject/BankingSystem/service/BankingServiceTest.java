package com.sampleProject.BankingSystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sampleProject.BankingSystem.dto.AccountDtos;
import com.sampleProject.BankingSystem.model.*;
import com.sampleProject.BankingSystem.repository.AccountRepository;
import com.sampleProject.BankingSystem.repository.CustomerRepository;
import com.sampleProject.BankingSystem.service.AccountService;
import com.sampleProject.BankingSystem.service.TransactionService;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("AccountServiceTest")
class BankingServiceTest { // keeping filename but testing AccountService

    @Mock CustomerRepository customerRepository;
    @Mock AccountRepository accountRepository;
    @Mock TransactionService transactionService;

    @InjectMocks AccountService accountService;

    private Customer customer;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("John");
        customer.setEmail("john@test.com");
    }

    private Account newAccount(Long id, BigDecimal balance, AccountType type) {
        Account a = new Account();
        a.setId(id);
        a.setCustomer(customer);
        a.setAccountType(type);
        a.setBalance(balance);
        a.setStatus("ACTIVE");
        return a;
    }

    @Test
    @DisplayName("createAccount with opening balance > 0 records opening deposit transaction")
    void createAccount_recordsOpeningTransaction() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> {
            Account a = inv.getArgument(0);
            if (a.getId()==null) a.setId(10L);
            return a;
        });
        when(transactionService.record(any(), any(), anyString(), any())).thenReturn(new BankTransaction());

        AccountDtos.CreateAccountRequest req = new AccountDtos.CreateAccountRequest();
        req.customerId = 1L;
        req.accountType = AccountType.SAVINGS;
        req.openingBalance = new BigDecimal("250.00");

        Account created = accountService.createAccount(req);
        assertNotNull(created.getId());
        assertEquals(new BigDecimal("250.00"), created.getBalance());
        verify(transactionService, times(1)).record(any(Account.class), eq(new BigDecimal("250.00")), eq("OPENING_DEPOSIT"), any());
    }

    @Test
    @DisplayName("deposit increases balance and records transaction")
    void deposit_ok() {
        Account acc = newAccount(5L, new BigDecimal("100.00"), AccountType.CURRENT);
        when(accountRepository.findWithLockingById(5L)).thenReturn(Optional.of(acc));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));
        when(transactionService.record(any(), any(), anyString(), any())).thenReturn(new BankTransaction());

        Account updated = accountService.deposit(5L, new BigDecimal("50.00"), "cash");
        assertEquals(new BigDecimal("150.00"), updated.getBalance());
        verify(transactionService).record(eq(acc), eq(new BigDecimal("50.00")), eq("DEPOSIT"), eq("cash"));
    }

    @Test
    @DisplayName("withdraw savings insufficient funds throws")
    void withdraw_insufficientSavings() {
        Account acc = newAccount(6L, new BigDecimal("40.00"), AccountType.SAVINGS);
        when(accountRepository.findWithLockingById(6L)).thenReturn(Optional.of(acc));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                accountService.withdraw(6L, new BigDecimal("50.00"), "atm")
        );
        assertTrue(ex.getMessage().toLowerCase().contains("insufficient"));
        verify(accountRepository, never()).save(any());
        verify(transactionService, never()).record(any(), any(), anyString(), any());
    }

    @Test
    @DisplayName("transfer moves funds and records in/out transactions")
    void transfer_ok() {
        Account from = newAccount(11L, new BigDecimal("200.00"), AccountType.CURRENT);
        from.setOverdraftLimit(new BigDecimal("100.00"));
        Account to = newAccount(12L, new BigDecimal("50.00"), AccountType.SAVINGS);

        when(accountRepository.findWithLockingById(11L)).thenReturn(Optional.of(from));
        when(accountRepository.findWithLockingById(12L)).thenReturn(Optional.of(to));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));
        when(transactionService.record(any(), any(), anyString(), any())).thenReturn(new BankTransaction());

        accountService.transfer(11L, 12L, new BigDecimal("75.00"), "xfer");

        assertEquals(new BigDecimal("125.00"), from.getBalance());
        assertEquals(new BigDecimal("125.00"), to.getBalance());
        verify(transactionService, times(1)).record(eq(from), eq(new BigDecimal("75.00")), eq("TRANSFER_OUT"), eq("xfer"));
        verify(transactionService, times(1)).record(eq(to), eq(new BigDecimal("75.00")), eq("TRANSFER_IN"), eq("xfer"));
    }

    @Test
    @DisplayName("cross-customer transfer now allowed and updates balances")
    void transfer_crossCustomerAllowed() {
        Customer other = new Customer();
        other.setId(2L);
        other.setFirstName("Alice");
        other.setEmail("alice@test.com");

        Account from = newAccount(31L, new BigDecimal("100.00"), AccountType.SAVINGS);
        Account to = newAccount(32L, new BigDecimal("50.00"), AccountType.CURRENT);
        to.setCustomer(other); // different owner

        when(accountRepository.findWithLockingById(31L)).thenReturn(Optional.of(from));
        when(accountRepository.findWithLockingById(32L)).thenReturn(Optional.of(to));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));
        when(transactionService.record(any(), any(), anyString(), any())).thenReturn(new BankTransaction());

        accountService.transfer(31L, 32L, new BigDecimal("25.00"), "ext");

        assertEquals(new BigDecimal("75.00"), from.getBalance());
        assertEquals(new BigDecimal("75.00"), to.getBalance());
        verify(transactionService, times(1)).record(eq(from), eq(new BigDecimal("25.00")), eq("TRANSFER_OUT"), eq("ext"));
        verify(transactionService, times(1)).record(eq(to), eq(new BigDecimal("25.00")), eq("TRANSFER_IN"), eq("ext"));
    }

    @Nested
    class Validation {
        @Test
        void deposit_negativeAmountThrows() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                    accountService.deposit(1L, new BigDecimal("-1"), "bad")
            );
            assertTrue(ex.getMessage().toLowerCase().contains("positive"));
        }
    }
}
