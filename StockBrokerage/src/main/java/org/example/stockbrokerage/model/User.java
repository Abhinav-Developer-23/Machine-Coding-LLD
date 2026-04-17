package org.example.stockbrokerage.model;

import lombok.Getter;

/**
 * A platform user identified uniquely by {@code userId}.
 *
 * <p>Immutable value object — signup data never changes. Wallet and portfolio for this user are
 * modelled as separate aggregates so that trading state can evolve independently of identity.
 */
@Getter
public class User {
  private final String userId;
  private final String name;

  public User(String userId, String name) {
    this.userId = userId;
    this.name = name;
  }

  @Override
  public String toString() {
    return "User{userId=" + userId + ", name=" + name + "}";
  }
}
