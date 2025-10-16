package com.sampleProject.BankingSystem.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class AdultValidator implements ConstraintValidator<Adult, LocalDate> {
    private int min;

    @Override
    public void initialize(Adult constraintAnnotation) {
        this.min = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) return true; // @NotNull handled elsewhere
        LocalDate today = LocalDate.now();
        if (value.isAfter(today)) return false;
        int age = Period.between(value, today).getYears();
        return age >= min;
    }
}

