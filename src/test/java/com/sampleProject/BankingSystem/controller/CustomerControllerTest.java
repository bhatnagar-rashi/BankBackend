package com.sampleProject.BankingSystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sampleProject.BankingSystem.controller.CustomerController;
import com.sampleProject.BankingSystem.model.Customer;
import com.sampleProject.BankingSystem.service.CustomerService;

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

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CustomerService customerService;

    private Customer customerEntity() {
        Customer c = new Customer();
        c.setId(1L);
        c.setFirstName("Alice");
        c.setLastName("Smith");
        c.setEmail("alice@test.com");
        c.setPhone("1234567890");
        c.setDob(LocalDate.of(1990,1,1));
        return c;
    }

    @Test
    @DisplayName("POST /api/v1/users creates customer and returns 201")
    void createCustomer_ok() throws Exception {
        Mockito.when(customerService.createCustomer(any())).thenReturn(customerEntity());
        String json = "{" +
                "\"firstName\":\"Alice\"," +
                "\"lastName\":\"Smith\"," +
                "\"email\":\"alice@test.com\"," +
                "\"phone\":\"1234567890\"," +
                "\"dob\":\"1990-01-01\"," +
                "\"password\":\"Secret123\"}";

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Alice")))
                .andExpect(jsonPath("$.email", is("alice@test.com")));
    }

    @Test
    @DisplayName("POST /api/v1/users validation error returns 400 with error payload")
    void createCustomer_validationError() throws Exception {
        String json = "{" +
                "\"email\":\"x@test.com\"," +
                "\"password\":\"abc123\"}";
        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("validation_error")));
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} returns customer")
    void getCustomer_ok() throws Exception {
        Mockito.when(customerService.getCustomer(1L)).thenReturn(customerEntity());
        mockMvc.perform(get("/api/v1/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Alice")));
    }

    @Test
    @DisplayName("POST /api/v1/users rejects underage user")
    void createCustomer_underage() throws Exception {
        String json = "{" +
                "\"firstName\":\"Bob\"," +
                "\"lastName\":\"Teen\"," +
                "\"email\":\"bobteen@test.com\"," +
                "\"phone\":\"1234567890\"," +
                "\"dob\":\"2015-01-01\"," +
                "\"password\":\"Abc123\"}";
        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("validation_error")));
    }

    @Test
    @DisplayName("POST /api/v1/users rejects invalid phone")
    void createCustomer_invalidPhone() throws Exception {
        String json = "{" +
                "\"firstName\":\"Carl\"," +
                "\"lastName\":\"Phone\"," +
                "\"email\":\"carlphone@test.com\"," +
                "\"phone\":\"12345\"," +
                "\"dob\":\"1990-01-01\"," +
                "\"password\":\"Abc123\"}";
        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("validation_error")));
    }
}
