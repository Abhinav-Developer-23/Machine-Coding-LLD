package org.example.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.example.models.Position;
import org.example.observers.GameObserver;
import org.example.service.SnakeFoodGameService;
import org.example.strategies.FixedFoodProviderStrategy;
import org.example.strategies.FoodProviderStrategy;
import org.example.strategies.RandomFoodProviderStrategy;

public final class SnakeFoodGameFactory {
  private SnakeFoodGameFactory() {}

  public static SnakeFoodGameService createWithFixedFood(
      int width, int height, List<Position> foodPositions, List<GameObserver> observers) {
    FoodProviderStrategy foodProviderStrategy = new FixedFoodProviderStrategy(foodPositions);
    return new SnakeFoodGameService(width, height, foodProviderStrategy, observers);
  }

  public static SnakeFoodGameService createWithRandomFood(
      int width, int height, Random random, List<GameObserver> observers) {
    FoodProviderStrategy foodProviderStrategy = new RandomFoodProviderStrategy(random);
    return new SnakeFoodGameService(width, height, foodProviderStrategy, observers);
  }

  public static SnakeFoodGameService createWithFixedFood(
      int width, int height, List<Position> foodPositions) {
    return createWithFixedFood(width, height, foodPositions, new ArrayList<>());
  }

  public static SnakeFoodGameService createWithRandomFood(int width, int height, Random random) {
    return createWithRandomFood(width, height, random, new ArrayList<>());
  }
}
