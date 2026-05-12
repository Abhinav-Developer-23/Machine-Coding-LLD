package org.example.strategies;

import java.util.Random;
import java.util.Set;
import org.example.models.Position;

public final class RandomFoodProviderStrategy implements FoodProviderStrategy {
  private final Random random;
  private final int maxAttempts;

  public RandomFoodProviderStrategy(Random random) {
    this(random, 10_000);
  }

  public RandomFoodProviderStrategy(Random random, int maxAttempts) {
    this.random = random;
    this.maxAttempts = maxAttempts;
  }

  @Override
  public Position nextFoodPosition(int width, int height, Set<Position> occupiedBySnake) {
    if (occupiedBySnake.size() >= width * height) {
      return null;
    }

    for (int attempt = 0; attempt < maxAttempts; attempt++) {
      int row = random.nextInt(height);
      int col = random.nextInt(width);
      Position candidate = new Position(row, col);
      if (!occupiedBySnake.contains(candidate)) {
        return candidate;
      }
    }

    for (int row = 0; row < height; row++) {
      for (int col = 0; col < width; col++) {
        Position candidate = new Position(row, col);
        if (!occupiedBySnake.contains(candidate)) {
          return candidate;
        }
      }
    }
    return null;
  }
}
