package com.lld.stockbrokerage.state;

import com.lld.stockbrokerage.model.Order;

public interface OrderState {
  void handle(Order order);

  void cancel(Order order);
}
