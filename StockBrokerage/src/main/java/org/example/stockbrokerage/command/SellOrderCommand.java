package org.example.stockbrokerage.command;

import org.example.stockbrokerage.model.Order;
import org.example.stockbrokerage.model.Portfolio;
import org.example.stockbrokerage.model.Wallet;

/**
 * Executes a SELL: removes shares from the portfolio and credits the wallet with the proceeds.
 *
 * <p>Holdings are decremented first so we can short-circuit on insufficient quantity without ever
 * touching the wallet. {@link Portfolio#remove} is atomic — it mutates only on success.
 */
public class SellOrderCommand implements OrderCommand {
  private final Order order;
  private final Wallet wallet;
  private final Portfolio portfolio;

  public SellOrderCommand(Order order, Wallet wallet, Portfolio portfolio) {
    this.order = order;
    this.wallet = wallet;
    this.portfolio = portfolio;
  }

  @Override
  public boolean execute() {
    if (!portfolio.remove(order.getSymbol(), order.getQuantity())) {
      return false;
    }
    wallet.credit(order.getTotalAmount());
    return true;
  }
}
