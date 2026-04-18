package org.example.delivery.strategy;

public interface DeliveryStrategy {
  int estimateDeliveryDays(String source, String destination);
}
