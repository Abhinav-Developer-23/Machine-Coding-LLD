package org.example.strategies;

/**
 * PaymentStrategy Interface (Strategy Pattern for Payments)
 *
 * <p>Defines the contract for processing payments using different methods (e.g., Debit Card, UPI,
 * Credit Card) interchangeably.
 */
public interface PaymentStrategy {
  boolean processPayment();
}
