package org.example.stockbrokerage.onlinestockbrokeragesystem.command;

import org.example.stockbrokerage.onlinestockbrokeragesystem.StockExchange;
import org.example.stockbrokerage.onlinestockbrokeragesystem.entities.Account;
import org.example.stockbrokerage.onlinestockbrokeragesystem.entities.Order;
import org.example.stockbrokerage.onlinestockbrokeragesystem.exceptions.InsufficientStockException;

public class SellStockCommand implements OrderCommand {
    private final Account account;
    private final Order order;
    private final StockExchange stockExchange;

    public SellStockCommand(Account account, Order order) {
        this.account = account;
        this.order = order;
        this.stockExchange = StockExchange.getInstance();
    }

    @Override
    public void execute() {
        if (account.getStockQuantity(order.getStock().getSymbol()) < order.getQuantity()) {
            throw new InsufficientStockException("Not enough stock to place sell order.");
        }
        System.out.printf("Placing SELL order %s for %d shares of %s.%n", order.getOrderId(), order.getQuantity(), order.getStock());
        stockExchange.placeSellOrder(order);
    }
}