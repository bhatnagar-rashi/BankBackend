package com.sampleProject.BankingSystem.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = AdultValidator.class)
@Documented
public @interface Adult {
    int min() default 18;
    String message() default "Must be at least {min} years old";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

