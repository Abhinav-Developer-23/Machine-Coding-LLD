package org.example.stockbrokerage.onlinestockbrokeragesystem.strategy;

import org.example.stockbrokerage.onlinestockbrokeragesystem.entities.Order;

public class MarketOrderStrategy implements ExecutionStrategy {
    @Override
    public boolean canExecute(Order order, double marketPrice) {
        return true; // Market orders can always execute
    }
}
