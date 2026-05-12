package org.example.enums;

import lombok.Getter;

@Getter
public enum Direction {
  UP(-1, 0),
  DOWN(1, 0),
  LEFT(0, -1),
  RIGHT(0, 1);

  private final int rowDelta;
  private final int colDelta;

  Direction(int rowDelta, int colDelta) {
    this.rowDelta = rowDelta;
    this.colDelta = colDelta;
  }
}
