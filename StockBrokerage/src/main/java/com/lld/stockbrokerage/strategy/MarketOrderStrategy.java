package com.lld.stockbrokerage.strategy;

import com.lld.stockbrokerage.model.Order;

public class MarketOrderStrategy implements ExecutionStrategy {
  @Override
  public boolean canExecute(Order order, double marketPrice) {
    return true; // Market orders can always execute
  }
}
