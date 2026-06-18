package shoppingcart.discount;

import java.util.ArrayList;
import java.util.List;
import shoppingcart.entities.CartItem;
import shoppingcart.enums.DiscountType;

public class CompositeDiscountStrategy implements DiscountStrategy {
  private final List<DiscountStrategy> strategies;

  public CompositeDiscountStrategy(List<DiscountStrategy> strategies) {
    this.strategies = new ArrayList<>(strategies);
  }

  @Override
  public double calculateDiscount(List<CartItem> items) {
    double totalDiscount = 0;
    for (DiscountStrategy strategy : strategies) {
      totalDiscount += strategy.calculateDiscount(items);
    }
    // Cap at subtotal to prevent negative totals
    double subtotal = items.stream().mapToDouble(CartItem::getSubtotal).sum();
    return Math.min(totalDiscount, subtotal);
  }

  @Override
  public String getDescription() {
    return strategies.stream()
        .map(DiscountStrategy::getDescription)
        .reduce((a, b) -> a + " + " + b)
        .orElse("No discounts");
  }

  @Override
  public DiscountType getType() {
    return DiscountType.PERCENTAGE; // Could add a COMPOSITE type
  }
}
