package com.lld.stockbrokerage.state;

import com.lld.stockbrokerage.model.Order;

public class CancelledState implements OrderState {
  @Override
  public void handle(Order order) {
    System.out.println("Order is cancelled.");
  }

  @Override
  public void cancel(Order order) {
    System.out.println("Order is already cancelled.");
  }
}
