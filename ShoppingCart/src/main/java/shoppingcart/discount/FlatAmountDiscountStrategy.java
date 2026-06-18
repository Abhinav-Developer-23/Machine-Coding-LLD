package shoppingcart.discount;

import java.util.List;
import shoppingcart.entities.CartItem;
import shoppingcart.enums.DiscountType;

public class FlatAmountDiscountStrategy implements DiscountStrategy {
  private final double amount;

  public FlatAmountDiscountStrategy(double amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Discount amount cannot be negative");
    }
    this.amount = amount;
  }

  @Override
  public double calculateDiscount(List<CartItem> items) {
    double subtotal = items.stream().mapToDouble(CartItem::getSubtotal).sum();
    // Cap at subtotal to prevent negative totals
    return Math.min(amount, subtotal);
  }

  @Override
  public String getDescription() {
    return "$" + String.format("%.2f", amount) + " off";
  }

  @Override
  public DiscountType getType() {
    return DiscountType.FLAT_AMOUNT;
  }
}
