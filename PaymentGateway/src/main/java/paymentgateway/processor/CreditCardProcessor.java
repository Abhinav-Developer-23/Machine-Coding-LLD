package paymentgateway.processor;

import paymentgateway.enums.PaymentStatus;
import paymentgateway.model.PaymentRequest;
import paymentgateway.model.PaymentResponse;

public class


CreditCardProcessor extends AbstractPaymentProcessor {
  @Override
  protected PaymentResponse doProcess(PaymentRequest request) {
    System.out.println(
        "Processing credit card payment of amount "
            + request.getAmount()
            + " "
            + request.getCurrency());
    // Simulate interaction with Visa/Mastercard network
    return new PaymentResponse(PaymentStatus.SUCCESSFUL, "Credit Card payment successful.");
  }
}
