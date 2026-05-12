package org.example.strategies;

import java.util.Set;
import org.example.models.Position;

public interface FoodProviderStrategy {
  Position nextFoodPosition(int width, int height, Set<Position> occupiedBySnake);
}
