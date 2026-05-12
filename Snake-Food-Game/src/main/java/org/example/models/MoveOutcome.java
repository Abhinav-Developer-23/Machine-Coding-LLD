package org.example.models;

import lombok.Getter;
import org.example.enums.GameStatus;

@Getter
public final class MoveOutcome {
  private final GameStatus status;
  private final int score;
  private final String message;

  public MoveOutcome(GameStatus status, int score, String message) {
    this.status = status;
    this.score = score;
    this.message = message;
  }
}
