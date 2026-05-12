package org.example.observers;

import org.example.enums.Direction;
import org.example.models.GameSnapshot;

public interface GameObserver {
  void onGameStarted(GameSnapshot snapshot);

  void onSnakeMoved(GameSnapshot snapshot, Direction direction);

  void onFoodEaten(GameSnapshot snapshot);

  void onGameOver(GameSnapshot snapshot, String reason);
}
