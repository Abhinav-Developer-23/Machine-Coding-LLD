package paymentgateway.factory;

import paymentgateway.enums.PaymentMethod;
import paymentgateway.processor.CreditCardProcessor;
import paymentgateway.processor.PayPalProcessor;
import paymentgateway.processor.PaymentProcessor;
import paymentgateway.processor.UPIProcessor;

public class PaymentProcessorFactory {
  public static PaymentProcessor getProcessor(PaymentMethod method) {
    return switch (method) {
      case CREDIT_CARD -> new CreditCardProcessor();
      case UPI -> new UPIProcessor();
      case PAYPAL -> new PayPalProcessor();
      // case BANK_TRANSFER -> new BankTransferProcessor();
      default -> throw new IllegalArgumentException("Unsupported payment method: " + method);
    };
  }
}
