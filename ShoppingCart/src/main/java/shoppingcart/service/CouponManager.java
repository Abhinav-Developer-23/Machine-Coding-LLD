package shoppingcart.service;

import java.util.concurrent.ConcurrentHashMap;
import shoppingcart.discount.DiscountStrategy;
import shoppingcart.entities.Coupon;
import shoppingcart.exceptions.CartException;

public class CouponManager {
  private final ConcurrentHashMap<String, Coupon> coupons = new ConcurrentHashMap<>();

  public void registerCoupon(Coupon coupon) {
    coupons.put(coupon.getCode(), coupon);
  }

  public DiscountStrategy redeemCoupon(String code) {
    Coupon coupon = coupons.get(code);
    if (coupon == null) {
      throw new CartException("Invalid coupon code: " + code);
    }
    if (!coupon.isValid()) {
      throw new CartException("Coupon expired: " + code);
    }
    return coupon.getStrategy();
  }
}
