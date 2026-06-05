package org.example.splitwise.entities;

import java.util.UUID;
import lombok.Getter;

@Getter
public class User {
  private final String id;
  private final String name;
  private final String email;
  private final BalanceSheet balanceSheet;

  public User(String name, String email) {
    this.id = UUID.randomUUID().toString();
    this.name = name;
    this.email = email;
    this.balanceSheet = new BalanceSheet(this);
  }
}
