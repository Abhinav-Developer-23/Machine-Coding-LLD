package shoppingcart.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import shoppingcart.discount.DiscountStrategy;
import shoppingcart.enums.CartStatus;
import shoppingcart.exceptions.CartException;
import shoppingcart.observer.CartObserver;

public class Cart {
  private final String id;
  private final Customer customer;
  private final ConcurrentHashMap<String, CartItem> items;
  private final ConcurrentHashMap<String, CartItem> savedItems;
  private volatile CartStatus status;
  private volatile DiscountStrategy discountStrategy;
  private final CopyOnWriteArrayList<CartObserver> observers;

  public Cart(String id, Customer customer) {
    this.id = id;
    this.customer = customer;
    this.items = new ConcurrentHashMap<>();
    this.savedItems = new ConcurrentHashMap<>();
    this.status = CartStatus.ACTIVE;
    this.discountStrategy = null;
    this.observers = new CopyOnWriteArrayList<>();
  }

  public synchronized void addItem(Product product, int quantity) {
    validateActive();
    if (quantity <= 0) {
      throw new CartException("Quantity must be positive");
    }

    CartItem existing = items.get(product.getId());
    int currentQuantity = (existing != null) ? existing.getQuantity() : 0;
    int newTotal = currentQuantity + quantity;

    if (newTotal > product.getMaxQuantityPerCart()) {
      throw new CartException(
          "Cannot add "
              + quantity
              + " units of "
              + product.getName()
              + ". Maximum allowed: "
              + product.getMaxQuantityPerCart()
              + ", currently in cart: "
              + currentQuantity);
    }

    if (existing != null) {
      existing.setQuantity(newTotal);
    } else {
      // Capture price at the moment of addition
      existing = new CartItem(product, quantity, product.getPrice());
      items.put(product.getId(), existing);
    }

    notifyItemAdded(existing);
  }

  public synchronized void removeItem(String productId) {
    validateActive();
    CartItem removed = items.remove(productId);
    if (removed == null) {
      throw new CartException("Product " + productId + " not found in cart");
    }
    notifyItemRemoved(removed);
  }

  public synchronized void updateItemQuantity(String productId, int newQuantity) {
    validateActive();
    CartItem item = items.get(productId);
    if (item == null) {
      throw new CartException("Product " + productId + " not found in cart");
    }
    if (newQuantity <= 0) {
      removeItem(productId);
      return;
    }
    if (newQuantity > item.getProduct().getMaxQuantityPerCart()) {
      throw new CartException(
          "Cannot set quantity to "
              + newQuantity
              + ". Maximum allowed: "
              + item.getProduct().getMaxQuantityPerCart());
    }
    item.setQuantity(newQuantity);
  }

  public synchronized void applyDiscount(DiscountStrategy strategy) {
    validateActive();
    this.discountStrategy = strategy;
  }

  public synchronized void removeDiscount() {
    validateActive();
    this.discountStrategy = null;
  }

  public synchronized void checkout() {
    validateActive();
    if (items.isEmpty()) {
      throw new CartException("Cannot checkout an empty cart");
    }
    this.status = CartStatus.CHECKED_OUT;
    notifyCheckedOut();
  }

  public synchronized void abandon() {
    validateActive();
    this.status = CartStatus.ABANDONED;
  }

  public synchronized void saveForLater(String productId) {
    validateActive();
    CartItem item = items.remove(productId);
    if (item == null) {
      throw new CartException("Product " + productId + " not found in cart");
    }
    savedItems.put(productId, item);
  }

  public synchronized void moveToCart(String productId) {
    validateActive();
    CartItem item = savedItems.remove(productId);
    if (item == null) {
      throw new CartException("Product " + productId + " not found in saved items");
    }
    items.put(productId, item);
    notifyItemAdded(item);
  }

  public double getSubtotal() {
    return items.values().stream().mapToDouble(CartItem::getSubtotal).sum();
  }

  public double getTotal() {
    double subtotal = getSubtotal();
    if (discountStrategy != null) {
      double discount = discountStrategy.calculateDiscount(new ArrayList<>(items.values()));
      return Math.max(0, subtotal - discount);
    }
    return subtotal;
  }

  public void addObserver(CartObserver observer) {
    observers.add(observer);
  }

  private void validateActive() {
    if (status != CartStatus.ACTIVE) {
      throw new CartException("Cannot modify cart. Cart status is " + status);
    }
  }

  private void notifyItemAdded(CartItem item) {
    for (CartObserver observer : observers) {
      observer.onItemAdded(this, item);
    }
  }

  private void notifyItemRemoved(CartItem item) {
    for (CartObserver observer : observers) {
      observer.onItemRemoved(this, item);
    }
  }

  private void notifyCheckedOut() {
    for (CartObserver observer : observers) {
      observer.onCartCheckedOut(this);
    }
  }

  public String getId() {
    return id;
  }

  public Customer getCustomer() {
    return customer;
  }

  public CartStatus getStatus() {
    return status;
  }

  public Map<String, CartItem> getItems() {
    return Collections.unmodifiableMap(items);
  }

  public Map<String, CartItem> getSavedItems() {
    return Collections.unmodifiableMap(savedItems);
  }
}
