package shoppingcart.service;

public class CartExpirationService {
  private final ShoppingCartService cartService;
  private final long expirationMillis;

  public CartExpirationService(ShoppingCartService cartService, long expirationMillis) {
    this.cartService = cartService;
    this.expirationMillis = expirationMillis;
  }

  public void expireStaleCarts() {
    long now = System.currentTimeMillis();
    // In a real system, ShoppingCartService would expose an iterator over active carts.
    // For each active cart, check if its AbandonedCartAlertObserver's
    // lastActivityTime is older than (now - expirationMillis).
    // If so, call cart.abandon().
  }
}
