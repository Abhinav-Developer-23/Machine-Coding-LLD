package org.example.strategies;

import java.util.List;

import org.example.models.Seat;

public interface PricingStrategy {
  double calculatePrice(List<Seat> seats);
}
