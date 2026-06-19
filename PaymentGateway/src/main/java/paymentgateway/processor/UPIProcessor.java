package paymentgateway.processor;

import paymentgateway.enums.PaymentStatus;
import paymentgateway.model.PaymentRequest;
import paymentgateway.model.PaymentResponse;

public class UPIProcessor extends AbstractPaymentProcessor {
  @Override
  protected PaymentResponse doProcess(PaymentRequest request) {
    System.out.println(
        "Processing UPI payment of " + request.getAmount() + " " + request.getCurrency());
    return new PaymentResponse(PaymentStatus.SUCCESSFUL, "UPI payment successful.");
  }
}
