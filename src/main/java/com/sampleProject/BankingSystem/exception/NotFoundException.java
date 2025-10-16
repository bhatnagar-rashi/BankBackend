package com.sampleProject.BankingSystem.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException() { super("The requested resource was not found."); }
    public NotFoundException(String message){ super(message); }
}
