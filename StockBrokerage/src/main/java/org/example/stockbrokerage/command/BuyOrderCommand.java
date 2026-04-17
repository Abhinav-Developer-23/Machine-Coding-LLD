package org.example.stockbrokerage.command;

import org.example.stockbrokerage.model.Order;
import org.example.stockbrokerage.model.Portfolio;
import org.example.stockbrokerage.model.Wallet;

/**
 * Executes a BUY: debits the wallet by {@code quantity * price} and adds shares to the portfolio.
 *
 * <p>The funds check happens first — if the wallet can't cover the cost, we return false without
 * touching either wallet or portfolio (spec: state unchanged on failure).
 */
public class BuyOrderCommand implements OrderCommand {
  private final Order order;
  private final Wallet wallet;
  private final Portfolio portfolio;

  public BuyOrderCommand(Order order, Wallet wallet, Portfolio portfolio) {
    this.order = order;
    this.wallet = wallet;
    this.portfolio = portfolio;
  }

  @Override
  public boolean execute() {
    int totalCost = order.getTotalAmount();
    if (!wallet.canAfford(totalCost)) {
      return false;
    }
    wallet.debit(totalCost);
    portfolio.add(order.getSymbol(), order.getQuantity());
    return true;
  }
}
