package org.example;

import java.util.ArrayList;
import java.util.List;
import org.example.enums.Direction;
import org.example.enums.GameStatus;
import org.example.factories.SnakeFoodGameFactory;
import org.example.models.MoveOutcome;
import org.example.models.Position;
import org.example.observers.ConsoleGameObserver;
import org.example.observers.GameObserver;
import org.example.service.SnakeFoodGameService;

public final class SnakeFoodGameDemo {
  private SnakeFoodGameDemo() {}

  public static void main(String[] args) {
    int width = 8;
    int height = 6;

    List<Position> foodPositions = new ArrayList<>();
    foodPositions.add(new Position(0, 3));
    foodPositions.add(new Position(2, 3));
    foodPositions.add(new Position(2, 4));
    foodPositions.add(new Position(4, 4));

    List<GameObserver> observers = List.of(new ConsoleGameObserver());
    SnakeFoodGameService service =
        SnakeFoodGameFactory.createWithFixedFood(width, height, foodPositions, observers);

    Direction[] moves = {
      Direction.RIGHT,
      Direction.RIGHT,
      Direction.RIGHT,
      Direction.DOWN,
      Direction.DOWN,
      Direction.LEFT,
      Direction.LEFT,
      Direction.RIGHT,
      Direction.RIGHT,
      Direction.DOWN,
      Direction.DOWN
    };

    for (Direction direction : moves) {
      MoveOutcome outcome = service.move(direction);
      if (outcome.getStatus() == GameStatus.GAME_OVER) {
        break;
      }
    }
  }
}
