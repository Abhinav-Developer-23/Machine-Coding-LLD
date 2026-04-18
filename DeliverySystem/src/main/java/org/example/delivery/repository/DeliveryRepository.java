package org.example.delivery.repository;

import java.util.concurrent.ConcurrentHashMap;
import org.example.delivery.model.Product;
import org.example.delivery.model.Warehouse;

public class DeliveryRepository {
  public static final ConcurrentHashMap<String, Product> productMap = new ConcurrentHashMap<>();
  public static final ConcurrentHashMap<String, Warehouse> warehouseMap = new ConcurrentHashMap<>();
}
