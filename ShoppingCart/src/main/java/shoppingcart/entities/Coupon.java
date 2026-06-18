package shoppingcart.entities;

import shoppingcart.discount.DiscountStrategy;
import shoppingcart.exceptions.CartException;

public class Coupon {
  private final String code;
  private final DiscountStrategy strategy;
  private final long expiryTime;

  public Coupon(String code, DiscountStrategy strategy, long expiryTime) {
    this.code = code;
    this.strategy = strategy;
    this.expiryTime = expiryTime;
  }

  public boolean isValid() {
    return System.currentTimeMillis() < expiryTime;
  }

  public String getCode() {
    return code;
  }

  public DiscountStrategy getStrategy() {
    return strategy;
  }
}
