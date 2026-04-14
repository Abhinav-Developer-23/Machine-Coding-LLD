package org.example.models;

import java.util.ArrayList;
import java.util.List;
import org.example.enums.Color;
import org.example.pieces.Bishop;
import org.example.pieces.King;
import org.example.pieces.Knight;
import org.example.pieces.Pawn;
import org.example.pieces.Piece;
import org.example.pieces.Queen;
import org.example.pieces.Rook;

public class Board {
  public static final int SIZE = 8;
  private final Cell[][] grid;

  public Board() {
    grid = new Cell[SIZE][SIZE];
    for (int r = 0; r < SIZE; r++) {
      for (int c = 0; c < SIZE; c++) {
        grid[r][c] = new Cell(r, c);
      }
    }
    initializePieces();
  }

  private void initializePieces() {
    // Black pieces (top rows)
    placePiece(new Rook(Color.BLACK), 0, 0);
    placePiece(new Knight(Color.BLACK), 0, 1);
    placePiece(new Bishop(Color.BLACK), 0, 2);
    placePiece(new Queen(Color.BLACK), 0, 3);
    placePiece(new King(Color.BLACK), 0, 4);
    placePiece(new Bishop(Color.BLACK), 0, 5);
    placePiece(new Knight(Color.BLACK), 0, 6);
    placePiece(new Rook(Color.BLACK), 0, 7);
    for (int c = 0; c < SIZE; c++) {
      placePiece(new Pawn(Color.BLACK), 1, c);
    }

    // White pieces (bottom rows)
    placePiece(new Rook(Color.WHITE), 7, 0);
    placePiece(new Knight(Color.WHITE), 7, 1);
    placePiece(new Bishop(Color.WHITE), 7, 2);
    placePiece(new Queen(Color.WHITE), 7, 3);
    placePiece(new King(Color.WHITE), 7, 4);
    placePiece(new Bishop(Color.WHITE), 7, 5);
    placePiece(new Knight(Color.WHITE), 7, 6);
    placePiece(new Rook(Color.WHITE), 7, 7);
    for (int c = 0; c < SIZE; c++) {
      placePiece(new Pawn(Color.WHITE), 6, c);
    }
  }

  private void placePiece(Piece piece, int row, int col) {
    grid[row][col].setPiece(piece);
  }

  public Cell getCell(int row, int col) {
    return grid[row][col];
  }

  public boolean isValidPosition(int row, int col) {
    return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
  }

  /**
   * Applies the move on the board and returns the captured piece (or null). The move object is
   * updated with the captured piece so it can be undone later.
   */
  public Piece applyMove(Move move) {
    Cell from = grid[move.getFromRow()][move.getFromCol()];
    Cell to = grid[move.getToRow()][move.getToCol()];

    Piece captured = to.getPiece();
    move.setCapturedPiece(captured);

    to.setPiece(from.getPiece());
    from.setPiece(null);

    return captured;
  }

  /** Undoes a move previously applied via applyMove. */
  public void undoMove(Move move) {
    Cell from = grid[move.getFromRow()][move.getFromCol()];
    Cell to = grid[move.getToRow()][move.getToCol()];

    from.setPiece(to.getPiece());
    to.setPiece(move.getCapturedPiece());
  }

  /** Finds the king of the given color and returns its [row, col], or null if not found. */
  public int[] findKing(Color color) {
    for (int r = 0; r < SIZE; r++) {
      for (int c = 0; c < SIZE; c++) {
        Piece piece = grid[r][c].getPiece();
        if (piece instanceof King && piece.getColor() == color) {
          return new int[] {r, c};
        }
      }
    }
    return null;
  }

  /**
   * Checks if the king of the given color is under attack. Uses raw move targets (no recursion) to
   * avoid infinite loops.
   */
  public boolean isInCheck(Color color) {
    int[] kingPos = findKing(color);
    if (kingPos == null) return false;

    Color opponent = color.opponent();
    for (int r = 0; r < SIZE; r++) {
      for (int c = 0; c < SIZE; c++) {
        Piece piece = grid[r][c].getPiece();
        if (piece != null && piece.getColor() == opponent) {
          for (int[] target : piece.getRawMoveTargets(this, r, c)) {
            if (target[0] == kingPos[0] && target[1] == kingPos[1]) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  /** Returns true if the given color has no legal moves (used for checkmate/stalemate). */
  public boolean hasNoLegalMoves(Color color) {
    for (int r = 0; r < SIZE; r++) {
      for (int c = 0; c < SIZE; c++) {
        Piece piece = grid[r][c].getPiece();
        if (piece != null && piece.getColor() == color) {
          if (!piece.getValidMoves(this, r, c).isEmpty()) {
            return false;
          }
        }
      }
    }
    return true;
  }

  /** Returns all legal moves for the given color. */
  public List<Move> getAllLegalMoves(Color color) {
    List<Move> moves = new ArrayList<>();
    for (int r = 0; r < SIZE; r++) {
      for (int c = 0; c < SIZE; c++) {
        Piece piece = grid[r][c].getPiece();
        if (piece != null && piece.getColor() == color) {
          moves.addAll(piece.getValidMoves(this, r, c));
        }
      }
    }
    return moves;
  }

  public void display() {
    System.out.println();
    System.out.println("    a  b  c  d  e  f  g  h");
    System.out.println("  +------------------------+");
    for (int r = 0; r < SIZE; r++) {
      System.out.printf("%d | ", 8 - r);
      for (int c = 0; c < SIZE; c++) {
        Piece piece = grid[r][c].getPiece();
        if (piece == null) {
          System.out.print(".  ");
        } else {
          System.out.print(piece.getSymbol() + "  ");
        }
      }
      System.out.printf("| %d%n", 8 - r);
    }
    System.out.println("  +------------------------+");
    System.out.println("    a  b  c  d  e  f  g  h");
    System.out.println();
  }
}
