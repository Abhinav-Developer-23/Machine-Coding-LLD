package org.example.models;

import java.util.UUID;

import org.example.enums.PaymentStatus;

import lombok.Getter;

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
