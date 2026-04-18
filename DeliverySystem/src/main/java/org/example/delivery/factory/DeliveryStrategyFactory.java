package org.example.delivery.factory;

import org.example.delivery.enums.DeliveryOption;
import org.example.delivery.external.DistanceService;
import org.example.delivery.strategy.AirDeliveryStrategy;
import org.example.delivery.strategy.DeliveryStrategy;
import org.example.delivery.strategy.GroundDeliveryStrategy;

public class DeliveryStrategyFactory {

  private final DistanceService distanceService;

  public DeliveryStrategyFactory(DistanceService distanceService) {
    this.distanceService = distanceService;
  }

  public DeliveryStrategy getStrategy(DeliveryOption option, boolean expressSupported) {

    if (option == DeliveryOption.EXPRESS && expressSupported) {
      return new AirDeliveryStrategy();
    }

    return new GroundDeliveryStrategy(distanceService);
  }
}
