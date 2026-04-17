package org.example.stockbrokerage.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.example.stockbrokerage.command.OrderCommand;
import org.example.stockbrokerage.command.OrderCommandFactory;
import org.example.stockbrokerage.enums.OrderType;
import org.example.stockbrokerage.model.Order;
import org.example.stockbrokerage.model.Portfolio;
import org.example.stockbrokerage.model.Stock;
import org.example.stockbrokerage.model.User;
import org.example.stockbrokerage.model.Wallet;
import org.example.stockbrokerage.repository.StockBrokerageRepository;

/**
 * All business logic for the BuyStocks platform.
 *
 * <p>Follows the project's layered convention: one service class, one repository, collaborating
 * with the Command/Factory/Builder layers. Every public method either returns {@code true}/data on
 * success, or returns {@code false}/sentinel on validation failure with <b>no state change</b> —
 * this is the spec's atomicity contract.
 *
 * <p>Validation is performed up front so that by the time we hand off to the Command layer we know
 * the inputs are well-formed; the commands are responsible only for the business invariants they
 * own (sufficient funds, sufficient holdings).
 */
public class BuyStocksService {
  /** Symbols may contain only uppercase letters A-Z and hyphens, per the spec. */
  private static final Pattern SYMBOL_PATTERN = Pattern.compile("[A-Z\\-]+");

  private static final int MAX_USER_ID_LEN = 30;
  private static final int MAX_NAME_LEN = 60;
  private static final int MAX_SYMBOL_LEN = 10;
  // Spec uses strict inequalities: 1 < initialWalletAmount < 900_000.
  private static final int MIN_WALLET_EXCLUSIVE = 1;
  private static final int MAX_WALLET_EXCLUSIVE = 900_000;
  // Spec uses strict inequalities: 0 < price < 100_000.
  private static final int MIN_PRICE_EXCLUSIVE = 0;
  private static final int MAX_PRICE_EXCLUSIVE = 100_000;
  // Spec: 1 <= quantity < 10_000 for buys.
  private static final int MAX_BUY_QUANTITY_EXCLUSIVE = 10_000;

  private final StockBrokerageRepository repository;
  private final OrderCommandFactory commandFactory;

  public BuyStocksService(StockBrokerageRepository repository) {
    this.repository = repository;
    this.commandFactory = new OrderCommandFactory();
  }

  /**
   * Creates a new user with a wallet and an empty portfolio.
   *
   * @return false if the userId is blank/duplicate, the name is invalid, or the wallet amount is
   *     out of range; true on success.
   */
  public boolean signUp(String userId, String name, int initialWalletAmount) {
    if (!isValidUserId(userId)) {
      return false;
    }
    if (!isValidName(name)) {
      return false;
    }
    if (initialWalletAmount <= MIN_WALLET_EXCLUSIVE || initialWalletAmount >= MAX_WALLET_EXCLUSIVE) {
      return false;
    }
    if (repository.userExists(userId)) {
      return false;
    }

    // All three aggregates are created together so later trade operations can always assume a
    // wallet and portfolio exist for any known user — saves null checks in the Command layer.
    repository.saveUser(new User(userId, name));
    repository.saveWallet(new Wallet(userId, initialWalletAmount));
    repository.savePortfolio(new Portfolio(userId));
    return true;
  }

  /**
   * Admin operation: add a new stock to the available list, or update an existing stock's price.
   *
   * @return false if the symbol format is invalid or the price is out of range; true on success.
   */
  public boolean adminAddOrUpdateStock(String symbol, int price) {
    if (!isValidSymbol(symbol)) {
      return false;
    }
    if (price <= MIN_PRICE_EXCLUSIVE || price >= MAX_PRICE_EXCLUSIVE) {
      return false;
    }

    Stock existing = repository.findStock(symbol);
    if (existing != null) {
      existing.setPrice(price);
    } else {
      repository.saveStock(new Stock(symbol, price));
    }
    return true;
  }

  /** Returns all stocks as "SYMBOL PRICE" strings, sorted lexicographically by symbol. */
  public List<String> listAvailableStocks() {
    // Copy into a TreeMap keyed by symbol to get lexicographic ordering for free, then format.
    Map<String, Stock> sortedStocks = new TreeMap<>();
    for (Stock stock : repository.findAllStocks()) {
      sortedStocks.put(stock.getSymbol(), stock);
    }
    List<String> result = new ArrayList<>();
    for (Stock stock : sortedStocks.values()) {
      result.add(stock.getSymbol() + " " + stock.getPrice());
    }
    return result;
  }

  /**
   * Places a buy order at the stock's current price.
   *
   * <p>Execution is delegated to a {@link OrderCommand} built by the factory; this method only
   * performs input validation and lookup.
   *
   * @return false on invalid input, unknown user/symbol, or insufficient funds; true on success.
   */
  public boolean buyStock(String userId, String symbol, int quantity) {
    if (quantity < 1 || quantity >= MAX_BUY_QUANTITY_EXCLUSIVE) {
      return false;
    }
    if (!isValidUserId(userId) || !repository.userExists(userId)) {
      return false;
    }
    Stock stock = repository.findStock(symbol);
    if (stock == null) {
      return false;
    }

    Order order =
        Order.builder()
            .userId(userId)
            .symbol(symbol)
            .quantity(quantity)
            .price(stock.getPrice())
            .orderType(OrderType.BUY)
            .build();

    OrderCommand command =
        commandFactory.create(
            order, repository.findWallet(userId), repository.findPortfolio(userId));
    return command.execute();
  }

  /**
   * Places a sell order at the stock's current price.
   *
   * @return false on invalid input, unknown user/symbol, or insufficient holdings; true on success.
   */
  public boolean sellStock(String userId, String symbol, int quantity) {
    if (quantity < 1) {
      return false;
    }
    if (!isValidUserId(userId) || !repository.userExists(userId)) {
      return false;
    }
    Stock stock = repository.findStock(symbol);
    if (stock == null) {
      return false;
    }

    Order order =
        Order.builder()
            .userId(userId)
            .symbol(symbol)
            .quantity(quantity)
            .price(stock.getPrice())
            .orderType(OrderType.SELL)
            .build();

    OrderCommand command =
        commandFactory.create(
            order, repository.findWallet(userId), repository.findPortfolio(userId));
    return command.execute();
  }

  /** Returns the wallet balance, or -1 if the user does not exist (spec sentinel). */
  public int getWalletBalance(String userId) {
    Wallet wallet = repository.findWallet(userId);
    return wallet == null ? -1 : wallet.getBalance();
  }

  /**
   * Returns the user's holdings as "SYMBOL QUANTITY" strings, sorted by symbol.
   *
   * <p>Returns an empty list both when the user exists but holds nothing, and when the user does
   * not exist at all — the spec explicitly conflates these two cases.
   */
  public List<String> getPortfolio(String userId) {
    Portfolio portfolio = repository.findPortfolio(userId);
    if (portfolio == null) {
      return Collections.emptyList();
    }
    // Portfolio is already a TreeMap, so iteration is already sorted by symbol.
    List<String> result = new ArrayList<>();
    for (Map.Entry<String, Integer> entry : portfolio.getHoldings().entrySet()) {
      int quantity = entry.getValue();
      if (quantity > 0) {
        result.add(entry.getKey() + " " + quantity);
      }
    }
    return result;
  }

  private boolean isValidUserId(String userId) {
    return userId != null && !userId.isBlank() && userId.length() <= MAX_USER_ID_LEN;
  }

  private boolean isValidName(String name) {
    return name != null && !name.isBlank() && name.length() <= MAX_NAME_LEN;
  }

  private boolean isValidSymbol(String symbol) {
    return symbol != null
        && !symbol.isBlank()
        && symbol.length() <= MAX_SYMBOL_LEN
        && SYMBOL_PATTERN.matcher(symbol).matches();
  }
}
