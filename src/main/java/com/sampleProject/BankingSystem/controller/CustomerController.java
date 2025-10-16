package com.sampleProject.BankingSystem.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.sampleProject.BankingSystem.dto.CustomerDtos;
import com.sampleProject.BankingSystem.model.Customer;
import com.sampleProject.BankingSystem.service.CustomerService;

@RestController
@RequestMapping("/api/v1/customers")
@CrossOrigin(origins = {"http://localhost:4300","http://localhost:4200"}, allowCredentials = "true")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDtos.CustomerResponse create(@RequestBody @Valid CustomerDtos.CreateCustomerRequest req) {
        Customer c = customerService.createCustomer(req);
        return toDto(c);
    }

    @GetMapping("/{id}")
    public CustomerDtos.CustomerResponse get(@PathVariable Long id) {
        return toDto(customerService.getCustomer(id));
    }

    @PutMapping("/{id}/profile")
    public CustomerDtos.CustomerResponse update(@PathVariable Long id, @RequestBody @Valid CustomerDtos.UpdateProfileRequest req) {
        Customer updated = customerService.updateProfile(id, req);
        return toDto(updated);
    }

    private static CustomerDtos.CustomerResponse toDto(Customer c) {
        CustomerDtos.CustomerResponse dto = new CustomerDtos.CustomerResponse();
        dto.setId(c.getId());
        dto.setFirstName(c.getFirstName());
        dto.setLastName(c.getLastName());
        dto.setEmail(c.getEmail());
        dto.setPhone(c.getPhone());
        dto.setDob(c.getDob());
        dto.setCreatedAt(c.getCreatedAt());
        dto.setAddressLine1(c.getAddressLine1());
        dto.setAddressLine2(c.getAddressLine2());
        dto.setCity(c.getCity());
        dto.setState(c.getState());
        dto.setPostalCode(c.getPostalCode());
        dto.setCountry(c.getCountry());
        dto.setUpdatedAt(c.getUpdatedAt());
        return dto;
    }
}
