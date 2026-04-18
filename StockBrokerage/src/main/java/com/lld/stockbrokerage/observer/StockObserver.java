package com.lld.stockbrokerage.observer;

import com.lld.stockbrokerage.model.Stock;

public interface StockObserver {
  void update(Stock stock);
}
