package shoppingcart.discount;

import java.util.List;
import shoppingcart.entities.CartItem;
import shoppingcart.enums.DiscountType;

public interface DiscountStrategy {
  double calculateDiscount(List<CartItem> items);

  String getDescription();

  DiscountType getType();
}
