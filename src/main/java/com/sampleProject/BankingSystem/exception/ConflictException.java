package com.sampleProject.BankingSystem.exception;

class ConflictException extends RuntimeException {
    public ConflictException() {
        super("Conflict detected. Resource already exists.");
    }

    public ConflictException(String message) {
        super(message);
    }
}
