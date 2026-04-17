package org.example.stockbrokerage.onlinestockbrokeragesystem.exceptions;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
