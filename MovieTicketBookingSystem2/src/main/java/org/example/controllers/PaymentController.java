package org.example.controllers;

import org.example.models.User;
import org.example.services.PaymentService;

public class PaymentController {
  // Service to handle payment-related logic
  private final PaymentService paymentService;

  public PaymentController(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  public void processPayment(final String bookingId, final User user) throws Exception {
    paymentService.processPayment(bookingId, user);
  }
}
