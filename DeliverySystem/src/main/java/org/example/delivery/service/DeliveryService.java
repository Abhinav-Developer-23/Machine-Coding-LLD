package org.example.delivery.service;

import org.example.delivery.enums.DeliveryOption;
import org.example.delivery.factory.DeliveryStrategyFactory;
import org.example.delivery.model.Product;
import org.example.delivery.model.Warehouse;
import org.example.delivery.repository.DeliveryRepository;
import org.example.delivery.strategy.DeliveryStrategy;

public class DeliveryService {

  private final DeliveryStrategyFactory strategyFactory;

  public DeliveryService(DeliveryStrategyFactory strategyFactory) {
    this.strategyFactory = strategyFactory;
  }

  public boolean canDeliverInTwoDays(
      String productId, String destinationPincode, DeliveryOption option) {

    Product product = DeliveryRepository.productMap.get(productId);
    if (product == null) {
      return false;
    }

    Warehouse warehouse = DeliveryRepository.warehouseMap.get(product.getWarehouseId());
    if (warehouse == null) {
      return false;
    }

    DeliveryStrategy strategy = strategyFactory.getStrategy(option, warehouse.isSupportsExpress());

    int days = strategy.estimateDeliveryDays(warehouse.getPincode(), destinationPincode);

    return days <= 2;
  }
}
