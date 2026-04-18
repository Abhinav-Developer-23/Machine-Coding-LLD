package org.example.delivery.strategy;

import org.example.delivery.external.DistanceService;

public class GroundDeliveryStrategy implements DeliveryStrategy {

  private final DistanceService distanceService;

  public GroundDeliveryStrategy(DistanceService distanceService) {
    this.distanceService = distanceService;
  }

  @Override
  public int estimateDeliveryDays(String source, String destination) {
    int distance = distanceService.getDistance(source, destination);

    if (distance <= 200) {
      return 1;
    }
    if (distance <= 800) {
      return 2;
    }
    return 4;
  }
}
