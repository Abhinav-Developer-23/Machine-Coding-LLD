package paymentgateway.processor;

import paymentgateway.enums.PaymentStatus;
import paymentgateway.model.PaymentRequest;
import paymentgateway.model.PaymentResponse;

public class PayPalProcessor extends AbstractPaymentProcessor {
  @Override
  protected PaymentResponse doProcess(PaymentRequest request) {
    System.out.println("Redirecting to PayPal for payer " + request.getPayerId());
    // Simulate PayPal API interaction
    return new PaymentResponse(PaymentStatus.SUCCESSFUL, "Paypal payment successful.");
  }
}
