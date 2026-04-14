package org.example.pieces;

import java.util.ArrayList;
import java.util.List;
import org.example.enums.Color;
import org.example.enums.PieceType;
import org.example.models.Board;

public class Pawn extends Piece {
  // WHITE moves toward row 0 (direction = -1), BLACK moves toward row 7 (direction = +1)
  private static final int WHITE_STARTING_ROW = 6;
  private static final int BLACK_STARTING_ROW = 1;

  public Pawn(Color color) {
    super(color, PieceType.PAWN);
  }

  @Override
  public String getSymbol() {
    return getColor() == Color.WHITE ? "P" : "p";
  }

  @Override
  public List<int[]> getRawMoveTargets(Board board, int row, int col) {
    List<int[]> targets = new ArrayList<>();
    int direction = (getColor() == Color.WHITE) ? -1 : 1;
    int startingRow = (getColor() == Color.WHITE) ? WHITE_STARTING_ROW : BLACK_STARTING_ROW;

    // One step forward
    int oneStep = row + direction;
    if (board.isValidPosition(oneStep, col) && board.getCell(oneStep, col).isEmpty()) {
      targets.add(new int[] {oneStep, col});

      // Two steps forward from starting position
      int twoStep = row + 2 * direction;
      if (row == startingRow
          && board.isValidPosition(twoStep, col)
          && board.getCell(twoStep, col).isEmpty()) {
        targets.add(new int[] {twoStep, col});
      }
    }

    // Diagonal captures
    for (int dc : new int[] {-1, 1}) {
      int captureCol = col + dc;
      if (board.isValidPosition(oneStep, captureCol)) {
        Piece target = board.getCell(oneStep, captureCol).getPiece();
        if (target != null && target.getColor() != getColor()) {
          targets.add(new int[] {oneStep, captureCol});
        }
      }
    }

    return targets;
  }
}
