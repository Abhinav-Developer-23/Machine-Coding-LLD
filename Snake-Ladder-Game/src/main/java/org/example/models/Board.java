package org.example.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class Board {
  private final int size;
  private final Map<Integer, Integer> snakesAndLadders;

  public Board(int size, List<BoardEntity> entities) {
    this.size = size;
    this.snakesAndLadders = new HashMap<>();

    for (BoardEntity entity : entities) {
      snakesAndLadders.put(entity.getStart(), entity.getEnd());
    }
  }

  /**
   * Resolves the final position after landing on a given square. If the position is the start of a
   * snake or ladder, returns the corresponding end position (tail for snakes, top for ladders).
   * Otherwise, returns the same position unchanged.
   *
   * @param position the square the player landed on (1 to size)
   * @return the final position after applying any snake or ladder at that square
   */
  public int getFinalPosition(int position) {
    return snakesAndLadders.getOrDefault(position, position);
  }
}
