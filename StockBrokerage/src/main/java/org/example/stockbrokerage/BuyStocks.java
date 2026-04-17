package org.example.stockbrokerage;

import java.util.List;

import org.example.stockbrokerage.repository.StockBrokerageRepository;
import org.example.stockbrokerage.service.BuyStocksService;

/**
 * Facade pattern: the public API of the platform, exactly matching the method signatures in the
 * problem statement.
 *
 * <p>Hides the underlying layered composition (service + repository + command/factory/builder
 * layers) from external callers — they only see {@code BuyStocks} and its seven methods. The
 * no-arg constructor wires a fresh repository and service so each instance is a self-contained
 * platform with no shared state.
 */
public class BuyStocks {
  private final BuyStocksService service;

  /** Initializes an empty platform with no users and no stocks. */
  public BuyStocks() {
    this.service = new BuyStocksService(new StockBrokerageRepository());
  }

  /** @see BuyStocksService#signUp(String, String, int) */
  public boolean signUp(String userId, String name, int initialWalletAmount) {
    return service.signUp(userId, name, initialWalletAmount);
  }

  /** @see BuyStocksService#adminAddOrUpdateStock(String, int) */
  public boolean adminAddOrUpdateStock(String symbol, int price) {
    return service.adminAddOrUpdateStock(symbol, price);
  }

  /** @see BuyStocksService#listAvailableStocks() */
  public List<String> listAvailableStocks() {
    return service.listAvailableStocks();
  }

  /** @see BuyStocksService#buyStock(String, String, int) */
  public boolean buyStock(String userId, String symbol, int quantity) {
    return service.buyStock(userId, symbol, quantity);
  }

  /** @see BuyStocksService#sellStock(String, String, int) */
  public boolean sellStock(String userId, String symbol, int quantity) {
    return service.sellStock(userId, symbol, quantity);
  }

  /** @see BuyStocksService#getWalletBalance(String) */
  public int getWalletBalance(String userId) {
    return service.getWalletBalance(userId);
  }

  /** @see BuyStocksService#getPortfolio(String) */
  public List<String> getPortfolio(String userId) {
    return service.getPortfolio(userId);
  }
}
