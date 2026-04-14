package org.example.pieces;

import java.util.ArrayList;
import java.util.List;
import org.example.enums.Color;
import org.example.enums.PieceType;
import org.example.models.Board;

public class Rook extends Piece {
  private static final int[][] DIRECTIONS = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

  public Rook(Color color) {
    super(color, PieceType.ROOK);
  }

  @Override
  public String getSymbol() {
    return getColor() == Color.WHITE ? "R" : "r";
  }

  @Override
  public List<int[]> getRawMoveTargets(Board board, int row, int col) {
    List<int[]> targets = new ArrayList<>();
    for (int[] dir : DIRECTIONS) {
      addSlidingMoves(board, row, col, dir[0], dir[1], targets);
    }
    return targets;
  }
}
