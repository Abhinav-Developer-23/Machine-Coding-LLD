package org.example.stockbrokerage.onlinestockbrokeragesystem.exceptions;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
