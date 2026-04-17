# Stock Brokerage System LLD

Layered machine-coding implementation of an online stock brokerage system.

## Layers

- `models`: domain objects such as `User`, `Account`, `Stock`, `Order`, and `Trade`.
- `repository`: `StockBrokerageRepository`, an in-memory map-backed database for users, stocks, orders, trades, and order books.
- `service`: `StockBrokerageService`, the single business service that exposes the application API.
- `strategy`: matching rule abstraction. `DefaultExecutionStrategy` matches the highest bid with the lowest ask when executable.
- `observer`: stock price subscription contract.

## Supported Flows

- Register users with cash accounts.
- Add stocks and update stock prices.
- Subscribe users to stock price updates.
- Place market and limit buy/sell orders.
- Cancel pending orders.
- Match orders across multiple stock symbols.
- Maintain user portfolios, cash balances, order status updates, and trade history.

## Important Design Choices

- The repository uses `ConcurrentHashMap` as a map database.
- The service methods that mutate order books are synchronized for simple thread safety.
- Limit buy orders reserve cash at placement time.
- Sell orders reserve stock quantity at placement time.
- Cancelling a pending order releases the remaining reserved cash or stock.
- Market orders are either matched immediately or cancelled for the unfilled quantity.

Run the sample flow from `org.example.StockBrokerageSystemDemo`.
