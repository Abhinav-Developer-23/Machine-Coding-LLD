package paymentgateway.observer;

import paymentgateway.model.Transaction;

public interface PaymentObserver {
  void onTransactionUpdate(Transaction transaction);
}
