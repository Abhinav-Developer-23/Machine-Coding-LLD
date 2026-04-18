package com.lld.stockbrokerage.service;

import com.lld.stockbrokerage.enums.OrderStatus;
import com.lld.stockbrokerage.enums.OrderType;
import com.lld.stockbrokerage.model.Order;
import com.lld.stockbrokerage.model.Stock;
import com.lld.stockbrokerage.model.User;
import com.lld.stockbrokerage.state.FilledState;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class StockExchange {
  private static volatile StockExchange instance;
  private final Map<String, List<Order>> buyOrders;
  private final Map<String, List<Order>> sellOrders;

  private StockExchange() {
    this.buyOrders = new ConcurrentHashMap<>();
    this.sellOrders = new ConcurrentHashMap<>();
  }

  public static StockExchange getInstance() {
    if (instance == null) {
      synchronized (StockExchange.class) {
        if (instance == null) {
          instance = new StockExchange();
        }
      }
    }
    return instance;
  }

  public void placeBuyOrder(Order order) {
    buyOrders
        .computeIfAbsent(order.getStock().getSymbol(), k -> new CopyOnWriteArrayList<>())
        .add(order);
    matchOrders(order.getStock());
  }

  public void placeSellOrder(Order order) {
    sellOrders
        .computeIfAbsent(order.getStock().getSymbol(), k -> new CopyOnWriteArrayList<>())
        .add(order);
    matchOrders(order.getStock());
  }

  /**
   * Order types handled here:
   *
   * <p>MARKET order — "buy/sell immediately at whatever the current market price is." The trader
   * doesn't set a price; they just want the trade done now. So when resolving price, we use
   * stock.getPrice() (the live market price).
   *
   * <p>LIMIT order — "only buy/sell if the price is at or better than MY stated price." The trader
   * sets a cap (for buys: max they'll pay) or a floor (for sells: min they'll accept). So we use
   * order.getPrice() directly.
   *
   * <p>WHY the price resolution below: A market buy has no stated price, so we treat its effective
   * bid as the current stock price. A market sell similarly has no floor, so its effective ask is
   * also the current stock price. This lets market orders always satisfy the buyPrice >= sellPrice
   * check and execute immediately against any open counter-order, which is exactly the semantics of
   * "execute at market."
   *
   * <p>WHY bestBuy uses the HIGHEST price and bestSell uses the LOWEST price: The buyer willing to
   * pay the most is the easiest to match — they are most likely to meet a seller's ask. The seller
   * asking the least is the easiest to match — they are most likely to meet a buyer's bid. Pairing
   * these two gives the best chance of a trade happening.
   */
  private void matchOrders(Stock stock) {
    synchronized (this) { // Critical section to prevent race conditions during matching
      List<Order> buys = buyOrders.get(stock.getSymbol());
      List<Order> sells = sellOrders.get(stock.getSymbol());

      if (buys == null || sells == null) return;

      boolean matchFound;
      do {
        matchFound = false;
        Order bestBuy = findBestBuy(buys);
        Order bestSell = findBestSell(sells);

        if (bestBuy != null && bestSell != null) {
          double buyPrice;
          if (bestBuy.getType() == OrderType.MARKET) {
            buyPrice = stock.getPrice();
          } else {
            buyPrice = bestBuy.getPrice();
          }
          double sellPrice;
          if (bestSell.getType() == OrderType.MARKET) {
            sellPrice = stock.getPrice();
          } else {
            sellPrice = bestSell.getPrice();
          }

          if (buyPrice >= sellPrice) {
            executeTrade(bestBuy, bestSell, sellPrice); // Trade at the seller's asking price
            matchFound = true;
          }
        }
      } while (matchFound);
    }
  }

  private void executeTrade(Order buyOrder, Order sellOrder, double tradePrice) {
    System.out.printf("--- Executing Trade for %s at $%.2f ---%n", buyOrder.getStock(), tradePrice);

    User buyer = buyOrder.getUser();
    User seller = sellOrder.getUser();

    int tradeQuantity = Math.min(buyOrder.getQuantity(), sellOrder.getQuantity());
    double totalCost = tradeQuantity * tradePrice;

    // Perform transaction
    buyer.getAccount().debit(totalCost);
    buyer.getAccount().addStock(buyOrder.getStock().getSymbol(), tradeQuantity);

    seller.getAccount().credit(totalCost);
    seller.getAccount().removeStock(sellOrder.getStock().getSymbol(), tradeQuantity);

    // Update orders
    updateOrderStatus(buyOrder, tradeQuantity);
    updateOrderStatus(sellOrder, tradeQuantity);

    // Update stock's market price to last traded price
    buyOrder.getStock().setPrice(tradePrice);

    System.out.println("--- Trade Complete ---");
  }

  private void updateOrderStatus(Order order, int quantityTraded) {
    // This is a simplified update logic. A real system would handle partial fills.
    order.setStatus(OrderStatus.FILLED);
    order.setState(new FilledState());
    String stockSymbol = order.getStock().getSymbol();
    // Remove from books
    if (buyOrders.get(stockSymbol) != null) buyOrders.get(stockSymbol).remove(order);
    if (sellOrders.get(stockSymbol) != null) sellOrders.get(stockSymbol).remove(order);
  }

  private Order findBestBuy(List<Order> buys) {
    Order best = null;
    for (Order o : buys) {
      if (o.getStatus() == OrderStatus.OPEN) {
        if (best == null || o.getPrice() > best.getPrice()) {
          best = o;
        }
      }
    }
    return best;
  }

  private Order findBestSell(List<Order> sells) {
    Order best = null;
    for (Order o : sells) {
      if (o.getStatus() == OrderStatus.OPEN) {
        if (best == null || o.getPrice() < best.getPrice()) {
          best = o;
        }
      }
    }
    return best;
  }
}
