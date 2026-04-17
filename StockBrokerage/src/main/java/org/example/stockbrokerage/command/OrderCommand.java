package org.example.stockbrokerage.command;

/**
 * Command pattern: encapsulates a single trade action (buy or sell) as an executable object.
 *
 * <p>Implementations own their own preconditions (sufficient funds, sufficient holdings) and
 * perform the state mutation atomically — either fully succeed and return {@code true}, or leave
 * wallet and portfolio untouched and return {@code false}. This keeps the service layer free of
 * buy/sell branching logic.
 */
public interface OrderCommand {
  /**
   * Executes the trade.
   *
   * @return {@code true} if the trade completed and state was updated; {@code false} if the
   *     precondition failed and state is unchanged.
   */
  boolean execute();
}
