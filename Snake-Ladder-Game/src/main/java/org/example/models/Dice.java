package org.example.models;

import lombok.Data;

import java.util.Random;

@Data
public class Dice {
  private final int minValue;
  private final int maxValue;
  private final Random random;

  public Dice(int minValue, int maxValue) {
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.random = new Random();
  }

  public int roll() {
    return random.nextInt(minValue, maxValue + 1);
  }
}
