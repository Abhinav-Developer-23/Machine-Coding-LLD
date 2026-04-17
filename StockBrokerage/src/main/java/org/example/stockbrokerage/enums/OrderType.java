package org.example.stockbrokerage.enums;

/**
 * Direction of a trade.
 *
 * <p>Used by the Command layer ({@code OrderCommandFactory}) to pick the concrete
 * {@code OrderCommand} implementation (buy vs sell) at runtime.
 */
public enum OrderType {
  /** Debits the wallet and adds shares to the portfolio. */
  BUY,
  /** Removes shares from the portfolio and credits the wallet. */
  SELL
}
