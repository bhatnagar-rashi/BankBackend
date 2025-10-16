package com.sampleProject.BankingSystem.exception;

class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException() {
        super("Transaction failed: insufficient funds.");
    }

    public InsufficientFundsException(String message) {
        super(message);
    }
}
