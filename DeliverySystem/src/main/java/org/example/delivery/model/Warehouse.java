package org.example.delivery.model;

import lombok.Getter;

@Getter
public class Warehouse {
  private final String id;
  private final String pincode;
  private final boolean supportsExpress;

  public Warehouse(String id, String pincode, boolean supportsExpress) {
    this.id = id;
    this.pincode = pincode;
    this.supportsExpress = supportsExpress;
  }
}
