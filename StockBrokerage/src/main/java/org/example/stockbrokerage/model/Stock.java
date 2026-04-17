package org.example.stockbrokerage.model;

import lombok.Getter;
import lombok.Setter;

/**
 * A tradable stock identified uniquely by {@code symbol}.
 *
 * <p>The symbol is immutable (acts as the primary key) but the price is mutable because admins may
 * update it during the day via {@code adminAddOrUpdateStock}. Trades execute at whatever price is
 * current at the moment {@code buyStock}/{@code sellStock} is invoked.
 */
@Getter
public class Stock {
  private final String symbol;

  @Setter private int price;

  public Stock(String symbol, int price) {
    this.symbol = symbol;
    this.price = price;
  }

  @Override
  public String toString() {
    return symbol + " " + price;
  }
}
