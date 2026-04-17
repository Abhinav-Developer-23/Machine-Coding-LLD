package org.example.stockbrokerage.model;

import org.example.stockbrokerage.enums.OrderType;

/**
 * Fluent builder for {@link Order} (Builder pattern).
 *
 * <p>Keeps the Order constructor package-private and lets callers assemble orders with named,
 * chainable setters rather than a positional multi-arg constructor — this makes the service-layer
 * call sites self-documenting.
 */
public class OrderBuilder {
  private String userId;
  private String symbol;
  private int quantity;
  private int price;
  private OrderType orderType;

  public OrderBuilder userId(String userId) {
    this.userId = userId;
    return this;
  }

  public OrderBuilder symbol(String symbol) {
    this.symbol = symbol;
    return this;
  }

  public OrderBuilder quantity(int quantity) {
    this.quantity = quantity;
    return this;
  }

  public OrderBuilder price(int price) {
    this.price = price;
    return this;
  }

  public OrderBuilder orderType(OrderType orderType) {
    this.orderType = orderType;
    return this;
  }

  public Order build() {
    return new Order(userId, symbol, quantity, price, orderType);
  }
}
