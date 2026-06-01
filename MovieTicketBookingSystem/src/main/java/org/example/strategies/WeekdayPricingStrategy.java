package org.example.strategies;

import java.util.List;

import org.example.models.Seat;

public class WeekdayPricingStrategy implements PricingStrategy {
  @Override
  public double calculatePrice(List<Seat> seats) {
    return seats.stream().mapToDouble(seat -> seat.getType().getPrice()).sum();
  }
}
