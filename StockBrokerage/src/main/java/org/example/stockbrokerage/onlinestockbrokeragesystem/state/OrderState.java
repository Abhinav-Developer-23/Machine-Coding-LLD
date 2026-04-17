package org.example.stockbrokerage.onlinestockbrokeragesystem.state;

import org.example.stockbrokerage.onlinestockbrokeragesystem.entities.Order;

public interface OrderState {
    void handle(Order order);
    void cancel(Order order);
}
