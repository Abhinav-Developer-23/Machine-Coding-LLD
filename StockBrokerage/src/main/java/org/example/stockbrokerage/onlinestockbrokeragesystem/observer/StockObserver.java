package org.example.stockbrokerage.onlinestockbrokeragesystem.observer;

import org.example.stockbrokerage.onlinestockbrokeragesystem.entities.Stock;

public interface StockObserver {
    void update(Stock stock);
}
