package com.sampleProject.BankingSystem.controller;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleValidation(MethodArgumentNotValidException ex){
        List<Map<String,String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toFieldError)
                .collect(Collectors.toList());
        Map<String,Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", 400);
        body.put("error", "validation_error");
        body.put("message", "Validation failed");
        body.put("fieldErrors", fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    private Map<String,String> toFieldError(FieldError fe){
        Map<String,String> m = new HashMap<>();
        m.put("field", fe.getField());
        m.put("message", fe.getDefaultMessage());
        return m;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,Object>> handleIllegalArgument(IllegalArgumentException ex){
        Map<String,Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", 400);
        body.put("error", "bad_request");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> conflict(IllegalStateException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "conflict");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String,Object>> handleConstraint(ConstraintViolationException ex){
        Map<String,Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", 400);
        body.put("error", "validation_error");
        body.put("message", "Validation failed");
        body.put("violations", ex.getConstraintViolations().stream().map(v -> {
            Map<String,String> m = new HashMap<>();
            m.put("property", v.getPropertyPath().toString());
            m.put("message", v.getMessage());
            return m;
        }).toList());
        return ResponseEntity.badRequest().body(body);
    }
}
