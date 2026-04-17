package org.example.stockbrokerage.model;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import lombok.Getter;

/**
 * Per-user stock holdings: a map of symbol to quantity owned.
 *
 * <p>Backed by a {@link TreeMap} so iteration order is already lexicographic by symbol — this
 * satisfies the spec's deterministic-output requirement without an extra sort step at read time.
 *
 * <p>Zero-quantity entries are proactively removed so {@link #getHoldings()} never exposes symbols
 * the user no longer owns. {@code getHoldings} returns an unmodifiable view, so we provide it
 * manually rather than relying on Lombok's default getter for the internal {@link TreeMap}.
 */
@Getter
public class Portfolio {
  private final String userId;
  private final Map<String, Integer> holdings;

  public Portfolio(String userId) {
    this.userId = userId;
    this.holdings = new TreeMap<>();
  }

  /** Read-only view of holdings, already sorted by symbol due to the underlying TreeMap. */
  public Map<String, Integer> getHoldings() {
    return Collections.unmodifiableMap(holdings);
  }

  public int quantityOf(String symbol) {
    return holdings.getOrDefault(symbol, 0);
  }

  /** Adds shares (used by buy). Creates the entry if the symbol is new. */
  public void add(String symbol, int quantity) {
    holdings.merge(symbol, quantity, Integer::sum);
  }

  /**
   * Removes shares (used by sell). Returns false and leaves state untouched if the user does not
   * own enough — this preserves the spec's "state unchanged on failure" contract.
   */
  public boolean remove(String symbol, int quantity) {
    int current = quantityOf(symbol);
    if (current < quantity) {
      return false;
    }
    int remaining = current - quantity;
    if (remaining == 0) {
      holdings.remove(symbol);
    } else {
      holdings.put(symbol, remaining);
    }
    return true;
  }
}
