package org.example.pieces;

import java.util.ArrayList;
import java.util.List;
import org.example.enums.Color;
import org.example.enums.PieceType;
import org.example.models.Board;

public class Knight extends Piece {
  private static final int[][] OFFSETS = {
    {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
    {1, -2}, {1, 2}, {2, -1}, {2, 1}
  };

  public Knight(Color color) {
    super(color, PieceType.KNIGHT);
  }

  @Override
  public String getSymbol() {
    return getColor() == Color.WHITE ? "N" : "n";
  }

  @Override
  public List<int[]> getRawMoveTargets(Board board, int row, int col) {
    List<int[]> targets = new ArrayList<>();
    for (int[] offset : OFFSETS) {
      int r = row + offset[0];
      int c = col + offset[1];
      if (board.isValidPosition(r, c)) {
        Piece occupant = board.getCell(r, c).getPiece();
        if (occupant == null || occupant.getColor() != getColor()) {
          targets.add(new int[] {r, c});
        }
      }
    }
    return targets;
  }
}
