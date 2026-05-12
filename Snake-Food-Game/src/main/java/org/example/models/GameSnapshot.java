package org.example.models;

import java.util.List;
import lombok.Getter;
import org.example.enums.GameStatus;

@Getter
public final class GameSnapshot {
  private final int width;
  private final int height;
  private final int score;
  private final GameStatus status;
  private final Position foodPosition;
  private final List<Position> snakeBody;

  public GameSnapshot(
      int width,
      int height,
      int score,
      GameStatus status,
      Position foodPosition,
      List<Position> snakeBody) {
    this.width = width;
    this.height = height;
    this.score = score;
    this.status = status;
    this.foodPosition = foodPosition;
    this.snakeBody = snakeBody;
  }
}
