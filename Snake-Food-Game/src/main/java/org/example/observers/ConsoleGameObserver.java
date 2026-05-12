package org.example.observers;

import java.util.List;
import org.example.enums.Direction;
import org.example.enums.GameStatus;
import org.example.models.GameSnapshot;
import org.example.models.Position;

public final class ConsoleGameObserver implements GameObserver {
  @Override
  public void onGameStarted(GameSnapshot snapshot) {
    System.out.println("Game started. Score=" + snapshot.getScore());
    render(snapshot);
  }

  @Override
  public void onSnakeMoved(GameSnapshot snapshot, Direction direction) {
    System.out.println("Move=" + direction + " Score=" + snapshot.getScore());
    render(snapshot);
  }

  @Override
  public void onFoodEaten(GameSnapshot snapshot) {
    System.out.println("Food eaten! Score=" + snapshot.getScore());
    render(snapshot);
  }

  @Override
  public void onGameOver(GameSnapshot snapshot, String reason) {
    System.out.println("Game over (" + reason + "). FinalScore=" + snapshot.getScore());
    render(snapshot);
  }

  private void render(GameSnapshot snapshot) {
    char[][] grid = new char[snapshot.getHeight()][snapshot.getWidth()];
    for (int row = 0; row < snapshot.getHeight(); row++) {
      for (int col = 0; col < snapshot.getWidth(); col++) {
        grid[row][col] = '.';
      }
    }

    Position food = snapshot.getFoodPosition();
    if (food != null) {
      grid[food.getRow()][food.getCol()] = 'F';
    }

    List<Position> body = snapshot.getSnakeBody();
    for (int i = 0; i < body.size(); i++) {
      Position p = body.get(i);
      grid[p.getRow()][p.getCol()] = i == 0 ? 'H' : 'S';
    }

    System.out.println("Status=" + snapshot.getStatus());
    for (int row = 0; row < snapshot.getHeight(); row++) {
      for (int col = 0; col < snapshot.getWidth(); col++) {
        System.out.print(grid[row][col]);
      }
      System.out.println();
    }
    if (snapshot.getStatus() == GameStatus.RUNNING) {
      System.out.println();
    }
  }
}
