package com.lld.stockbrokerage.strategy;

import com.lld.stockbrokerage.model.Order;

public interface ExecutionStrategy {
  boolean canExecute(Order order, double marketPrice);
}
