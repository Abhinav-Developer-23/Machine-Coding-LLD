package shoppingcart.observer;

import shoppingcart.entities.Cart;
import shoppingcart.entities.CartItem;

public class AbandonedCartAlertObserver implements CartObserver {
  private volatile long lastActivityTime;

  public AbandonedCartAlertObserver() {
    this.lastActivityTime = System.currentTimeMillis();
  }

  @Override
  public void onItemAdded(Cart cart, CartItem item) {
    lastActivityTime = System.currentTimeMillis();
  }

  @Override
  public void onItemRemoved(Cart cart, CartItem item) {
    lastActivityTime = System.currentTimeMillis();
  }

  @Override
  public void onCartCheckedOut(Cart cart) {
    // Cart checked out, no longer at risk of abandonment
    lastActivityTime = System.currentTimeMillis();
  }

  public long getLastActivityTime() {
    return lastActivityTime;
  }
}
