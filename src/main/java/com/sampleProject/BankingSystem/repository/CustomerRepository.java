package com.sampleProject.BankingSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sampleProject.BankingSystem.model.Customer;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
}
