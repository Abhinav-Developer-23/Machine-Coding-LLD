package org.example.service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.example.enums.Direction;
import org.example.enums.GameStatus;
import org.example.models.Food;
import org.example.models.GameSnapshot;
import org.example.models.MoveOutcome;
import org.example.models.Position;
import org.example.models.Snake;
import org.example.observers.GameObserver;
import org.example.strategies.FoodProviderStrategy;

public final class SnakeFoodGameService {
  private static final String DEFAULT_SNAKE_ID = "S1";
  private static final String DEFAULT_FOOD_ID = "F1";

  private final int width;
  private final int height;
  private final FoodProviderStrategy foodProviderStrategy;
  private final List<GameObserver> observers;

  private final Map<String, Snake> snakesById;
  private final Map<String, Deque<Position>> snakeBodyById;
  private final Map<String, Set<Position>> snakeOccupiedById;
  private final Map<String, Food> foodById;

  private GameStatus status;
  private int score;

  public SnakeFoodGameService(
      int width,
      int height,
      FoodProviderStrategy foodProviderStrategy,
      List<GameObserver> observers) {
    if (width <= 0 || height <= 0) {
      throw new IllegalArgumentException(
          "Invalid board size: width=" + width + " height=" + height);
    }
    this.width = width;
    this.height = height;
    this.foodProviderStrategy = foodProviderStrategy;
    this.observers = observers == null ? new ArrayList<>() : new ArrayList<>(observers);

    this.snakesById = new HashMap<>();
    this.snakeBodyById = new HashMap<>();
    this.snakeOccupiedById = new HashMap<>();
    this.foodById = new HashMap<>();

    initialize();
  }

  private void initialize() {
    this.status = GameStatus.RUNNING;
    this.score = 0;

    Snake snake = new Snake(DEFAULT_SNAKE_ID);
    snakesById.put(snake.getId(), snake);

    Deque<Position> body = new ArrayDeque<>();
    body.addFirst(new Position(0, 0));
    snakeBodyById.put(snake.getId(), body);

    Set<Position> occupied = new HashSet<>();
    occupied.add(new Position(0, 0));
    snakeOccupiedById.put(snake.getId(), occupied);

    spawnFoodIfNeeded();
    notifyGameStarted();
  }

  public MoveOutcome move(Direction direction) {
    if (direction == null) {
      throw new IllegalArgumentException("Direction cannot be null");
    }
    if (status == GameStatus.GAME_OVER) {
      return new MoveOutcome(status, score, "Game already over");
    }

    Deque<Position> body = snakeBodyById.get(DEFAULT_SNAKE_ID);
    Set<Position> occupied = snakeOccupiedById.get(DEFAULT_SNAKE_ID);
    Position currentHead = body.peekFirst();
    Position newHead = currentHead.translate(direction.getRowDelta(), direction.getColDelta());

    if (!isInsideBoard(newHead)) {
      status = GameStatus.GAME_OVER;
      GameSnapshot snapshot = snapshot();
      notifyGameOver(snapshot, "Hit wall at " + newHead);
      return new MoveOutcome(status, score, "Hit wall");
    }

    Position currentTail = body.peekLast();
    Position foodPosition = getActiveFoodPosition();
    boolean willEatFood = foodPosition != null && foodPosition.equals(newHead);

    boolean selfCollision =
        occupied.contains(newHead) && !(newHead.equals(currentTail) && !willEatFood);
    if (selfCollision) {
      status = GameStatus.GAME_OVER;
      GameSnapshot snapshot = snapshot();
      notifyGameOver(snapshot, "Hit itself at " + newHead);
      return new MoveOutcome(status, score, "Hit itself");
    }

    body.addFirst(newHead);
    occupied.add(newHead);

    if (willEatFood) {
      score++;
      foodById.clear();
      spawnFoodIfNeeded();
      GameSnapshot snapshot = snapshot();
      notifyFoodEaten(snapshot);
      notifySnakeMoved(snapshot, direction);
      return new MoveOutcome(status, score, "Food eaten");
    }

    Position removed = body.removeLast();
    occupied.remove(removed);

    GameSnapshot snapshot = snapshot();
    notifySnakeMoved(snapshot, direction);
    return new MoveOutcome(status, score, "Moved");
  }

  public GameSnapshot snapshot() {
    Deque<Position> body = snakeBodyById.get(DEFAULT_SNAKE_ID);
    List<Position> copy = new ArrayList<>(body);
    return new GameSnapshot(width, height, score, status, getActiveFoodPosition(), copy);
  }

  public GameStatus getStatus() {
    return status;
  }

  public int getScore() {
    return score;
  }

  private void spawnFoodIfNeeded() {
    if (!foodById.isEmpty()) {
      return;
    }

    Set<Position> occupied = snakeOccupiedById.get(DEFAULT_SNAKE_ID);
    Position nextFoodPosition = foodProviderStrategy.nextFoodPosition(width, height, occupied);
    if (nextFoodPosition == null) {
      return;
    }
    foodById.put(DEFAULT_FOOD_ID, new Food(DEFAULT_FOOD_ID, nextFoodPosition));
  }

  private Position getActiveFoodPosition() {
    Food active = foodById.get(DEFAULT_FOOD_ID);
    if (active == null) {
      return null;
    }
    return active.getPosition();
  }

  private boolean isInsideBoard(Position position) {
    return position.getRow() >= 0
        && position.getRow() < height
        && position.getCol() >= 0
        && position.getCol() < width;
  }

  private void notifyGameStarted() {
    GameSnapshot snapshot = snapshot();
    for (GameObserver observer : observers) {
      observer.onGameStarted(snapshot);
    }
  }

  private void notifySnakeMoved(GameSnapshot snapshot, Direction direction) {
    for (GameObserver observer : observers) {
      observer.onSnakeMoved(snapshot, direction);
    }
  }

  private void notifyFoodEaten(GameSnapshot snapshot) {
    for (GameObserver observer : observers) {
      observer.onFoodEaten(snapshot);
    }
  }

  private void notifyGameOver(GameSnapshot snapshot, String reason) {
    for (GameObserver observer : observers) {
      observer.onGameOver(snapshot, reason);
    }
  }
}
