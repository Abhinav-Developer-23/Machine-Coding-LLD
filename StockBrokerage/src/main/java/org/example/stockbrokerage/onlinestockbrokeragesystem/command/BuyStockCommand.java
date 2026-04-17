package org.example.stockbrokerage.onlinestockbrokeragesystem.command;

import org.example.stockbrokerage.onlinestockbrokeragesystem.StockExchange;
import org.example.stockbrokerage.onlinestockbrokeragesystem.entities.Account;
import org.example.stockbrokerage.onlinestockbrokeragesystem.entities.Order;
import org.example.stockbrokerage.onlinestockbrokeragesystem.enums.OrderType;
import org.example.stockbrokerage.onlinestockbrokeragesystem.exceptions.InsufficientFundsException;

public class BuyStockCommand implements OrderCommand {
    private final Account account;
    private final Order order;
    private final StockExchange stockExchange;

    public BuyStockCommand(Account account, Order order) {
        this.account = account;
        this.order = order;
        this.stockExchange = StockExchange.getInstance();
    }

    @Override
    public void execute() {
        // For market order, we can't pre-check funds perfectly.
        // For limit order, we can pre-authorize the amount.
        double estimatedCost = order.getQuantity() * order.getPrice();
        if (order.getType() == OrderType.LIMIT && account.getBalance() < estimatedCost) {
            throw new InsufficientFundsException("Not enough cash to place limit buy order.");
        }
        System.out.printf("Placing BUY order %s for %d shares of %s.%n", order.getOrderId(), order.getQuantity(), order.getStock());
        stockExchange.placeBuyOrder(order);
    }
}
