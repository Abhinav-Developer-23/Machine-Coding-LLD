package com.lld.stockbrokerage.state;

import com.lld.stockbrokerage.model.Order;

public class FilledState implements OrderState {
  @Override
  public void handle(Order order) {
    System.out.println("Order is already filled.");
  }

  @Override
  public void cancel(Order order) {
    System.out.println("Cannot cancel a filled order.");
  }
}
