package org.example.delivery.strategy;

public class AirDeliveryStrategy implements DeliveryStrategy {

  @Override
  public int estimateDeliveryDays(String source, String destination) {
    return 1;
  }
}
