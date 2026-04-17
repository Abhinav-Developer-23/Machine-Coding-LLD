package org.example.stockbrokerage.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.example.stockbrokerage.model.Portfolio;
import org.example.stockbrokerage.model.Stock;
import org.example.stockbrokerage.model.User;
import org.example.stockbrokerage.model.Wallet;

/**
 * Single in-memory data store for the platform (Repository pattern).
 *
 * <p>Holds all four domain maps — users, stocks, wallets, portfolios — behind save/find methods so
 * the service layer never deals with raw maps. This layout was chosen deliberately (over
 * one-repository-per-entity) to keep the LLD footprint small while still decoupling persistence
 * from business logic.
 *
 * <p>All maps use {@link HashMap} for O(1) lookup by key. The spec's "sorted output" requirement
 * is handled at the service layer on read, not here.
 */
public class StockBrokerageRepository {
  private final Map<String, User> users;
  private final Map<String, Stock> stocks;
  private final Map<String, Wallet> wallets;
  private final Map<String, Portfolio> portfolios;

  public StockBrokerageRepository() {
    this.users = new HashMap<>();
    this.stocks = new HashMap<>();
    this.wallets = new HashMap<>();
    this.portfolios = new HashMap<>();
  }

  public void saveUser(User user) {
    users.put(user.getUserId(), user);
  }

  public User findUser(String userId) {
    return users.get(userId);
  }

  public boolean userExists(String userId) {
    return users.containsKey(userId);
  }

  public void saveStock(Stock stock) {
    stocks.put(stock.getSymbol(), stock);
  }

  public Stock findStock(String symbol) {
    return stocks.get(symbol);
  }

  public Collection<Stock> findAllStocks() {
    return stocks.values();
  }

  public void saveWallet(Wallet wallet) {
    wallets.put(wallet.getUserId(), wallet);
  }

  public Wallet findWallet(String userId) {
    return wallets.get(userId);
  }

  public void savePortfolio(Portfolio portfolio) {
    portfolios.put(portfolio.getUserId(), portfolio);
  }

  public Portfolio findPortfolio(String userId) {
    return portfolios.get(userId);
  }
}
