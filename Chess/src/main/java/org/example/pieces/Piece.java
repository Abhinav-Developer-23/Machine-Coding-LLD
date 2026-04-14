package org.example.pieces;

import java.util.ArrayList;
import java.util.List;
import org.example.enums.Color;
import org.example.enums.PieceType;
import org.example.models.Board;
import org.example.models.Move;

public abstract class Piece {
  private final Color color;
  private final PieceType type;

  protected Piece(Color color, PieceType type) {
    this.color = color;
    this.type = type;
  }

  public Color getColor() {
    return color;
  }

  public PieceType getType() {
    return type;
  }

  /**
   * Returns the display symbol for this piece. Uppercase = WHITE, lowercase = BLACK (standard chess
   * notation).
   */
  public abstract String getSymbol();

  /**
   * Returns raw target positions [row, col] reachable by this piece without considering whether the
   * move leaves the own king in check. Used internally for check detection to avoid recursion.
   */
  public abstract List<int[]> getRawMoveTargets(Board board, int row, int col);

  /**
   * Returns fully legal moves by filtering raw moves that would leave the own king in check. Uses
   * make/unmake on the board to avoid allocating a copy.
   */
  public List<Move> getValidMoves(Board board, int row, int col) {
    List<Move> validMoves = new ArrayList<>();
    for (int[] target : getRawMoveTargets(board, row, col)) {
      Move move = new Move(row, col, target[0], target[1], this);
      board.applyMove(move);
      if (!board.isInCheck(color)) {
        validMoves.add(move);
      }
      board.undoMove(move);
    }
    return validMoves;
  }

  /** Helper used by sliding pieces (Rook, Bishop, Queen) to walk in one direction. */
  protected void addSlidingMoves(
      Board board, int row, int col, int dRow, int dCol, List<int[]> targets) {
    int r = row + dRow;
    int c = col + dCol;
    while (board.isValidPosition(r, c)) {
      Piece occupant = board.getCell(r, c).getPiece();
      if (occupant == null) {
        targets.add(new int[] {r, c});
      } else {
        if (occupant.getColor() != this.color) {
          targets.add(new int[] {r, c}); // capture
        }
        break; // blocked
      }
      r += dRow;
      c += dCol;
    }
  }
}
