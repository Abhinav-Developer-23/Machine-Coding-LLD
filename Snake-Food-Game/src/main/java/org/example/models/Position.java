package org.example.models;

import java.util.Objects;
import lombok.Getter;

@Getter
public final class Position {
  private final int row;
  private final int col;

  public Position(int row, int col) {
    this.row = row;
    this.col = col;
  }

  public Position translate(int rowDelta, int colDelta) {
    return new Position(row + rowDelta, col + colDelta);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Position position)) {
      return false;
    }
    return row == position.row && col == position.col;
  }

  @Override
  public int hashCode() {
    return Objects.hash(row, col);
  }

  @Override
  public String toString() {
    return "(" + row + "," + col + ")";
  }
}
