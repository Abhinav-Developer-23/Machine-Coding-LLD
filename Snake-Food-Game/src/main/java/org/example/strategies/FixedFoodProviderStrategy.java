package org.example.strategies;

import java.util.List;
import java.util.Set;
import org.example.models.Position;

public final class FixedFoodProviderStrategy implements FoodProviderStrategy {
  private final List<Position> foodPositions;
  private int nextIndex;

  public FixedFoodProviderStrategy(List<Position> foodPositions) {
    this.foodPositions = foodPositions;
    this.nextIndex = 0;
  }

  @Override
  public Position nextFoodPosition(int width, int height, Set<Position> occupiedBySnake) {
    if (nextIndex >= foodPositions.size()) {
      return null;
    }

    Position position = foodPositions.get(nextIndex);
    nextIndex++;

    if (position.getRow() < 0
        || position.getRow() >= height
        || position.getCol() < 0
        || position.getCol() >= width) {
      throw new IllegalStateException("Food position out of bounds: " + position);
    }
    if (occupiedBySnake.contains(position)) {
      throw new IllegalStateException("Food position overlaps snake body: " + position);
    }
    return position;
  }
}
