package org.example.stockbrokerage.model;

import lombok.Getter;
import org.example.stockbrokerage.enums.OrderType;

/**
 * Immutable snapshot of a trade request at the moment it is placed.
 *
 * <p>Captures the price at placement time so that later price updates by the admin do not retro-
 * actively change an executed trade. Constructed only via {@link OrderBuilder} (Builder pattern) —
 * the package-private constructor keeps callers from bypassing the builder.
 */
@Getter
public class Order {
  private final String userId;
  private final String symbol;
  private final int quantity;
  private final int price;
  private final OrderType orderType;

  Order(String userId, String symbol, int quantity, int price, OrderType orderType) {
    this.userId = userId;
    this.symbol = symbol;
    this.quantity = quantity;
    this.price = price;
    this.orderType = orderType;
  }

  /** Entry point for the Builder pattern. */
  public static OrderBuilder builder() {
    return new OrderBuilder();
  }

  /** Total cash value of this trade — quantity * price. */
  public int getTotalAmount() {
    return quantity * price;
  }
}
