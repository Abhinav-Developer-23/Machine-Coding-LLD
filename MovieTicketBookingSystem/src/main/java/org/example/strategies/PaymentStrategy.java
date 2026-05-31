package org.example.strategies;

import org.example.models.Payment;

public interface PaymentStrategy {
    Payment pay(double amount);
}
