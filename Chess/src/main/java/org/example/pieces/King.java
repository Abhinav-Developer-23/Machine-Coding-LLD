package org.example.pieces;

import java.util.ArrayList;
import java.util.List;
import org.example.enums.Color;
import org.example.enums.PieceType;
import org.example.models.Board;

public class King extends Piece {
  private static final int[][] OFFSETS = {
    {-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}
  };

  public King(Color color) {
    super(color, PieceType.KING);
  }

  @Override
  public String getSymbol() {
    return getColor() == Color.WHITE ? "K" : "k";
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
