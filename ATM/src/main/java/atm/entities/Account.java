package atm.entities;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class Account {
  private final String accountNumber;
  private double balance;
  private final Map<String, Card> cards;

  public Account(String accountNumber, double balance) {
    this.accountNumber = accountNumber;
    this.balance = balance;
    this.cards = new HashMap<>();
  }

  public synchronized void deposit(double amount) {
    balance += amount;
  }

  public synchronized boolean withdraw(double amount) {
    if (balance >= amount) {
      balance -= amount;
      return true;
    }
    return false;
  }
}
