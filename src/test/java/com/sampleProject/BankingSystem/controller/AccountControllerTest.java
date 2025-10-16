package com.sampleProject.BankingSystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sampleProject.BankingSystem.controller.AccountController;
import com.sampleProject.BankingSystem.model.*;
import com.sampleProject.BankingSystem.service.AccountService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doNothing;

@WebMvcTest(controllers = AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AccountControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean AccountService accountService;

    private Account sampleAccount(Long id, BigDecimal balance) {
        Account a = new Account();
        a.setId(id);
        a.setAccountNumber("123456789012");
        Customer c = new Customer();
        c.setId(7L);
        c.setFirstName("John");
        a.setCustomer(c);
        a.setAccountType(AccountType.SAVINGS);
        a.setBalance(balance);
        a.setOpenedAt(Instant.now());
        a.setStatus("ACTIVE");
        return a;
    }

    @Test
    @DisplayName("GET /api/v1/accounts/{id} returns account")
    void getAccount_ok() throws Exception {
        Mockito.when(accountService.getAccount(5L)).thenReturn(sampleAccount(5L, new BigDecimal("100.00")));
        mockMvc.perform(get("/api/v1/accounts/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.balance", is(100.00)))
                .andExpect(jsonPath("$.customerId", is(7)));
    }

    @Test
    @DisplayName("POST /api/v1/accounts creates account")
    void createAccount_ok() throws Exception {
        Mockito.when(accountService.createAccount(any())).thenReturn(sampleAccount(9L, new BigDecimal("250.00")));
        String json = "{\"customerId\":7,\"accountType\":\"SAVINGS\"}";
        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(9)))
                .andExpect(jsonPath("$.balance", is(250.00)));
    }

    @Test
    @DisplayName("POST /api/v1/accounts/{id}/deposit deposits funds")
    void deposit_ok() throws Exception {
        Mockito.when(accountService.deposit(eq(5L), any(), any())).thenReturn(sampleAccount(5L, new BigDecimal("150.00")));
        String json = "{\"amount\":50.00,\"note\":\"cash\"}";
        mockMvc.perform(post("/api/v1/accounts/5/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance", is(150.00)));
    }

    @Test
    @DisplayName("DELETE /api/v1/accounts/{id} closes account (204)")
    void closeAccount_ok() throws Exception {
        doNothing().when(accountService).deleteAccount(5L);
        mockMvc.perform(delete("/api/v1/accounts/5"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/accounts/{id} with non-zero balance returns conflict")
    void closeAccount_conflict() throws Exception {
        doThrow(new IllegalStateException("Account balance must be zero to close")).when(accountService).deleteAccount(6L);
        mockMvc.perform(delete("/api/v1/accounts/6"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", is("conflict")));
    }
}
