package org.example.stockbrokerage;

/**
 * Runnable demo that exercises the platform against the two examples from the problem statement.
 *
 * <p>Each example uses its own fresh {@link BuyStocks} instance so they don't share state — the
 * output should match the expected values line-for-line.
 */
public class BuyStocksDemo {
  public static void main(String[] args) {
    runExample1();
    System.out.println();
    runExample2();
  }

  private static void runExample1() {
    System.out.println("=== Example 1: Basic signup, buy, sell, and views ===");
    BuyStocks platform = new BuyStocks();

    print("adminAddOrUpdateStock(AAPL, 100)", platform.adminAddOrUpdateStock("AAPL", 100));
    print("adminAddOrUpdateStock(TSLA, 250)", platform.adminAddOrUpdateStock("TSLA", 250));
    print("signUp(u1, Asha, 1000)", platform.signUp("u1", "Asha", 1000));
    print("buyStock(u1, AAPL, 5)", platform.buyStock("u1", "AAPL", 5));
    print("getWalletBalance(u1)", platform.getWalletBalance("u1"));
    print("getPortfolio(u1)", platform.getPortfolio("u1"));
    print("sellStock(u1, AAPL, 2)", platform.sellStock("u1", "AAPL", 2));
    print("getWalletBalance(u1)", platform.getWalletBalance("u1"));
    print("getPortfolio(u1)", platform.getPortfolio("u1"));
    print("listAvailableStocks()", platform.listAvailableStocks());
  }

  private static void runExample2() {
    System.out.println("=== Example 2: Invalid operations and deterministic lists ===");
    BuyStocks platform = new BuyStocks();

    print("adminAddOrUpdateStock(GOOG, 300)", platform.adminAddOrUpdateStock("GOOG", 300));
    print("adminAddOrUpdateStock(AMZN, 200)", platform.adminAddOrUpdateStock("AMZN", 200));
    print("listAvailableStocks()", platform.listAvailableStocks());
    print("signUp(u2, Ravi, 250)", platform.signUp("u2", "Ravi", 250));
    // Duplicate userId: second signup must be a no-op returning false.
    print("signUp(u2, Ravi Again, 999)", platform.signUp("u2", "Ravi Again", 999));
    // NFLX is not in the admin's available list, so this fails without touching wallet.
    print("buyStock(u2, NFLX, 1)", platform.buyStock("u2", "NFLX", 1));
    // GOOG costs 300 but wallet holds only 250 — insufficient funds.
    print("buyStock(u2, GOOG, 1)", platform.buyStock("u2", "GOOG", 1));
    print("buyStock(u2, AMZN, 1)", platform.buyStock("u2", "AMZN", 1));
    print("getWalletBalance(u2)", platform.getWalletBalance("u2"));
    print("getPortfolio(u2)", platform.getPortfolio("u2"));
    // User owns 1 AMZN — selling 2 must fail atomically.
    print("sellStock(u2, AMZN, 2)", platform.sellStock("u2", "AMZN", 2));
    print("sellStock(u2, AMZN, 1)", platform.sellStock("u2", "AMZN", 1));
    print("getWalletBalance(u2)", platform.getWalletBalance("u2"));
    print("getPortfolio(u2)", platform.getPortfolio("u2"));
    // Unknown user: balance returns -1 sentinel, portfolio returns empty list.
    print("getWalletBalance(unknown)", platform.getWalletBalance("unknown"));
    print("getPortfolio(unknown)", platform.getPortfolio("unknown"));
  }

  private static void print(String label, Object result) {
    System.out.println(label + " -> " + result);
  }
}
