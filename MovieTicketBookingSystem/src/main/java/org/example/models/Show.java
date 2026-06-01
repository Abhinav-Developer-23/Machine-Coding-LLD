package org.example.models;

import java.time.LocalDateTime;

import org.example.strategies.PricingStrategy;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Show {
  private final String id;
  private final Movie movie;
  private final Screen screen;
  private final LocalDateTime startTime;
  private final PricingStrategy pricingStrategy;
}
