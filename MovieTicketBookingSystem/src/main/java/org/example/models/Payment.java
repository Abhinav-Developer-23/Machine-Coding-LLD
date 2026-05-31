package org.example.models;

import java.util.UUID;
import lombok.Getter;
import org.example.enums.PaymentStatus;

@Getter
public class Payment {
    private final String id;
    private final double amount;
    private final PaymentStatus status;
    private final String transactionId;

    public Payment(double amount, PaymentStatus status, String transactionId) {
        this.id = UUID.randomUUID().toString();
        this.amount = amount;
        this.status = status;
        this.transactionId = transactionId;
    }
}
