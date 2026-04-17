package org.example.stockbrokerage.command;

import org.example.stockbrokerage.enums.OrderType;
import org.example.stockbrokerage.model.Order;
import org.example.stockbrokerage.model.Portfolio;
import org.example.stockbrokerage.model.Wallet;

/**
 * Factory pattern: maps an {@link OrderType} to the matching {@link OrderCommand} implementation.
 *
 * <p>Isolates the buy-vs-sell branching in one place so that adding a new order type (e.g.
 * LIMIT, STOP_LOSS) only requires a new command class and a new branch here — the service layer
 * stays untouched.
 */
public class OrderCommandFactory {
  public OrderCommand create(Order order, Wallet wallet, Portfolio portfolio) {
    if (order.getOrderType() == OrderType.BUY) {
      return new BuyOrderCommand(order, wallet, portfolio);
    }
    return new SellOrderCommand(order, wallet, portfolio);
  }
}
