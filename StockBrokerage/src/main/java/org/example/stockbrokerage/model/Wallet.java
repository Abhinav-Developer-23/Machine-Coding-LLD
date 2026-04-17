package org.example.stockbrokerage.model;

import lombok.Getter;

/**
 * Per-user cash balance used to fund trades.
 *
 * <p>Each user owns exactly one wallet. Buying debits the wallet, selling credits it. The wallet
 * enforces the "sufficient funds" invariant via {@link #canAfford(int)} — callers (the Command
 * layer) must check first, since {@link #debit(int)} does not validate and will happily produce a
 * negative balance if misused.
 *
 * <p>Balances are stored as {@code int} because the spec mandates integer prices and amounts.
 * Mutation is exposed only through {@link #credit(int)}/{@link #debit(int)} — there is
 * intentionally no Lombok {@code @Setter} for {@code balance}.
 */
@Getter
public class Wallet {
  private final String userId;
  private int balance;

  public Wallet(String userId, int balance) {
    this.userId = userId;
    this.balance = balance;
  }

  /** Returns true if the wallet can cover the given amount (balance >= amount). */
  public boolean canAfford(int amount) {
    return balance >= amount;
  }

  /** Adds funds to the wallet — used on successful sell orders. */
  public void credit(int amount) {
    balance += amount;
  }

  /**
   * Removes funds from the wallet — used on successful buy orders. Caller must verify funds via
   * {@link #canAfford(int)} first.
   */
  public void debit(int amount) {
    balance -= amount;
  }
}
