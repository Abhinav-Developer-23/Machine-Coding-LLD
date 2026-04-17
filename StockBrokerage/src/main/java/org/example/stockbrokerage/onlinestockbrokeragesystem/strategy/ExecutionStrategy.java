package org.example.stockbrokerage.onlinestockbrokeragesystem.strategy;

import org.example.stockbrokerage.onlinestockbrokeragesystem.entities.Order;

public interface ExecutionStrategy {
    boolean canExecute(Order order, double marketPrice);
}
