package com.sampleProject.BankingSystem.exception;

class BadRequestException extends RuntimeException {
    public BadRequestException() {
        super("Invalid request. Please check your input.");
    }

    public BadRequestException(String message) {
        super(message);
    }
}
