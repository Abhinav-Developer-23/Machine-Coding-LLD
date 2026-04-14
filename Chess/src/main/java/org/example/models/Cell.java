package org.example.models;

import org.example.pieces.Piece;

public class Cell {
  private final int row;
  private final int col;
  private Piece piece;

  public Cell(int row, int col) {
    this.row = row;
    this.col = col;
    this.piece = null;
  }

  public int getRow() {
    return row;
  }

  public int getCol() {
    return col;
  }

  public Piece getPiece() {
    return piece;
  }

  public void setPiece(Piece piece) {
    this.piece = piece;
  }

  public boolean isEmpty() {
    return piece == null;
  }

  @Override
  public String toString() {
    return String.format("(%d,%d)", row, col);
  }
}
