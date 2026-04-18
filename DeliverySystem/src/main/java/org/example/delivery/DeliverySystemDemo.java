package org.example.delivery;

import org.example.delivery.enums.DeliveryOption;
import org.example.delivery.external.DistanceService;
import org.example.delivery.factory.DeliveryStrategyFactory;
import org.example.delivery.model.Product;
import org.example.delivery.model.Warehouse;
import org.example.delivery.repository.DeliveryRepository;
import org.example.delivery.service.DeliveryService;

public class DeliverySystemDemo {

  public static void main(String[] args) {

    Warehouse w1 = new Warehouse("W1", "110001", true);
    DeliveryRepository.warehouseMap.put(w1.getId(), w1);

    Product p1 = new Product("P1", "Laptop", "W1");
    DeliveryRepository.productMap.put(p1.getId(), p1);

    DeliveryService service =
        new DeliveryService(new DeliveryStrategyFactory(new DistanceService()));

    boolean result = service.canDeliverInTwoDays("P1", "560001", DeliveryOption.EXPRESS);

    System.out.println("Can deliver in 2 days: " + result);
  }
}
