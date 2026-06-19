package paymentgateway.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import paymentgateway.enums.PaymentStatus;

@Getter
public class Transaction {
  private final String id;
  private final PaymentRequest request;
  @Setter private PaymentStatus status;
  private final LocalDateTime timestamp;

  public Transaction(PaymentRequest request) {
    this.id = UUID.randomUUID().toString();
    this.request = request;
    this.status = PaymentStatus.INITIATED;
    this.timestamp = LocalDateTime.now();
  }
}
