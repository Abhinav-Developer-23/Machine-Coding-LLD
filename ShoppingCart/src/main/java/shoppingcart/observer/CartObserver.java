package shoppingcart.observer;

import shoppingcart.entities.Cart;
import shoppingcart.entities.CartItem;

public interface CartObserver {
  void onItemAdded(Cart cart, CartItem item);

  void onItemRemoved(Cart cart, CartItem item);

  void onCartCheckedOut(Cart cart);
}
