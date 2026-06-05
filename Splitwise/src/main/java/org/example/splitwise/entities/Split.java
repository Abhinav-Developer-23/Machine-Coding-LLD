package org.example.splitwise.entities;

import lombok.Getter;

@Getter
public class Split {
  private final User user;
  private final double amount;

  public Split(User user, double amount) {
    this.user = user;
    this.amount = amount;
  }
}
