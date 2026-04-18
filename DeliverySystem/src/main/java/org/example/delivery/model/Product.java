package org.example.delivery.model;

import lombok.Getter;

@Getter
public class Product {
  private final String id;
  private final String name;
  private final String warehouseId;

  public Product(String id, String name, String warehouseId) {
    this.id = id;
    this.name = name;
    this.warehouseId = warehouseId;
  }
}
