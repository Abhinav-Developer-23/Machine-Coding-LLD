package paymentgateway.processor;

import paymentgateway.model.PaymentRequest;
import paymentgateway.model.PaymentResponse;

public interface PaymentProcessor {
  PaymentResponse processPayment(PaymentRequest request);
}
