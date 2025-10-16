package com.sampleProject.BankingSystem.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sampleProject.BankingSystem.dto.CustomerDtos;
import com.sampleProject.BankingSystem.model.Customer;
import com.sampleProject.BankingSystem.repository.CustomerRepository;

import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CustomerService {
    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);
    private final CustomerRepository customerRepository;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{6,100}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Customer createCustomer(CustomerDtos.CreateCustomerRequest req) {
        log.info("Attempting to create customer email={} firstName={}", req.getEmail(), req.getFirstName());
        if (req.getEmail() == null) {
            log.warn("Rejecting create: email missing");
            throw new IllegalArgumentException("Email required");
        }
        // Normalize inputs
        String email = req.getEmail().trim().toLowerCase();
        String firstName = req.getFirstName() != null ? req.getFirstName().trim() : null;
        String lastName = req.getLastName() != null ? req.getLastName().trim() : null;
        String phone = req.getPhone() != null ? req.getPhone().trim() : null;
        String password = req.getPassword();

        if (firstName == null || firstName.isEmpty()) {
            log.warn("Rejecting create: firstName blank email={}", email);
            throw new IllegalArgumentException("First name required");
        }
        if (phone == null || !PHONE_PATTERN.matcher(phone).matches()) {
            log.warn("Rejecting create: invalid phone={} email={}", phone, email);
            throw new IllegalArgumentException("Phone must be exactly 10 digits");
        }
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            log.warn("Rejecting create: weak password email={}", email);
            throw new IllegalArgumentException("Password must be 6-100 chars and contain at least one letter and one digit");
        }

        customerRepository.findByEmail(email).ifPresent(c -> {
            log.warn("Rejecting create: duplicate email={}", email);
            throw new IllegalArgumentException("Email already registered");
        });
        Customer c = new Customer();
        c.setFirstName(firstName);
        c.setLastName(lastName);
        c.setEmail(email);
        c.setPhone(phone);
        c.setDob(req.getDob());
        c.setPassword(password);
        Customer saved = customerRepository.save(c);
        log.info("Created customer id={} email={}", saved.getId(), saved.getEmail());
        return saved;
    }

    public Customer getCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));
    }

    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email.trim().toLowerCase()).orElseThrow(() -> new IllegalArgumentException("Customer not found: " + email));
    }

    public Customer authenticate(String email, String password) {
        Customer c = findByEmail(email);
        if (c.getPassword() == null || !c.getPassword().equals(password)) {
            log.warn("Authentication failed for email={}", email);
            throw new IllegalArgumentException("Invalid credentials");
        }
        log.info("Authentication success for email={}", email);
        return c;
    }
    @Transactional
    public Customer updateProfile(Long id, com.sampleProject.BankingSystem.dto.CustomerDtos.UpdateProfileRequest req) {
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));
        String newEmail = req.getEmail().trim().toLowerCase();
        customerRepository.findByEmail(newEmail).ifPresent(other -> {
            if (!other.getId().equals(id)) {
                throw new IllegalArgumentException("Email already registered");
            }
        });
        if (!PHONE_PATTERN.matcher(req.getPhone()).matches()) {
            throw new IllegalArgumentException("Phone must be exactly 10 digits");
        }
        c.setFirstName(req.getFirstName().trim());
        c.setLastName(req.getLastName() != null ? req.getLastName().trim() : null);
        c.setEmail(newEmail);
        c.setPhone(req.getPhone().trim());
        c.setAddressLine1(req.getAddressLine1());
        c.setAddressLine2(req.getAddressLine2());
        c.setCity(req.getCity());
        c.setState(req.getState());
        c.setPostalCode(req.getPostalCode());
        c.setCountry(req.getCountry());
        return customerRepository.save(c);
    }
}
