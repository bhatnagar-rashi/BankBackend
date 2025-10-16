package com.sampleProject.BankingSystem.dto;

import jakarta.validation.constraints.*;
import java.time.Instant;
import java.time.LocalDate;

import com.sampleProject.BankingSystem.validation.Adult;

public class CustomerDtos {

    public static class CreateCustomerRequest {
        @NotBlank
        @Size(max = 100)
        private String firstName;
        @Size(max = 100)
        private String lastName;
        @Email @NotBlank @Size(max = 255)
        private String email;
        @NotBlank
        @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be exactly 10 digits")
        private String phone;
        @NotNull
        @Adult(min = 18, message = "Customer must be at least 18 years old")
        private LocalDate dob;
        @NotBlank
        @Size(min = 6, max = 100)
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{6,100}$", message = "Password must contain at least one letter and one digit")
        private String password;

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public LocalDate getDob() { return dob; }
        public void setDob(LocalDate dob) { this.dob = dob; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class CustomerResponse {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private LocalDate dob;
        private Instant createdAt;
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private String postalCode;
        private String country;
        private Instant updatedAt;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public LocalDate getDob() { return dob; }
        public void setDob(LocalDate dob) { this.dob = dob; }
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
        public String getAddressLine1() { return addressLine1; }
        public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }
        public String getAddressLine2() { return addressLine2; }
        public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public String getPostalCode() { return postalCode; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public Instant getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    }

    public static class UpdateProfileRequest {
        @NotBlank @Size(max=100)
        private String firstName;
        @Size(max=100)
        private String lastName;
        @Email @NotBlank @Size(max=255)
        private String email;
        @NotBlank @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be exactly 10 digits")
        private String phone;
        @Size(max=255)
        private String addressLine1;
        @Size(max=255)
        private String addressLine2;
        @Size(max=100)
        private String city;
        @Size(max=100)
        private String state;
        @Size(max=20)
        private String postalCode;
        @Size(max=100)
        private String country;

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getAddressLine1() { return addressLine1; }
        public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }
        public String getAddressLine2() { return addressLine2; }
        public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public String getPostalCode() { return postalCode; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
    }

    public static class LoginRequest {
        @Email @NotBlank
        private String email;
        @NotBlank
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
