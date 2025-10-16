package com.sampleProject.BankingSystem.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sampleProject.BankingSystem.dto.CustomerDtos;
import com.sampleProject.BankingSystem.model.Customer;
import com.sampleProject.BankingSystem.service.CustomerService;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = {"http://localhost:4200"}, allowCredentials = "true") // single origin
public class AuthController {

    private final CustomerService customerService;

    public AuthController(CustomerService customerService) {
        this.customerService = customerService;
    }

    private CustomerDtos.CustomerResponse toDto(Customer c){
        CustomerDtos.CustomerResponse d = new CustomerDtos.CustomerResponse();
        d.setId(c.getId());
        d.setFirstName(c.getFirstName());
        d.setLastName(c.getLastName());
        d.setEmail(c.getEmail());
        d.setPhone(c.getPhone());
        d.setDob(c.getDob());
        d.setCreatedAt(c.getCreatedAt());
        return d;
    }

    /**
     * Simplified login validating plain password (demo only).
     */
    @PostMapping("/login")
    public ResponseEntity<CustomerDtos.CustomerResponse> login(@RequestBody @Valid CustomerDtos.LoginRequest req){
        Customer c = customerService.authenticate(req.getEmail(), req.getPassword());
        return ResponseEntity.ok(toDto(c));
    }

    /**
     * No-op logout kept for compatibility.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(){
        return ResponseEntity.noContent().build();
    }
}
