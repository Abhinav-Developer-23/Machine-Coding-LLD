package com.lld.stockbrokerage.exception;

public class InsufficientFundsException extends RuntimeException {
  public InsufficientFundsException(String message) {
    super(message);
  }
}
