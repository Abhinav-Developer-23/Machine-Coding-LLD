package org.example.models;

import org.example.pieces.Piece;

public class Move {
  private final int fromRow;
  private final int fromCol;
  private final int toRow;
  private final int toCol;
  private final Piece piece;
  private Piece capturedPiece;

  public Move(int fromRow, int fromCol, int toRow, int toCol, Piece piece) {
    this.fromRow = fromRow;
    this.fromCol = fromCol;
    this.toRow = toRow;
    this.toCol = toCol;
    this.piece = piece;
  }

  public int getFromRow() {
    return fromRow;
  }

  public int getFromCol() {
    return fromCol;
  }

  public int getToRow() {
    return toRow;
  }

  public int getToCol() {
    return toCol;
  }

  public Piece getPiece() {
    return piece;
  }

  public Piece getCapturedPiece() {
    return capturedPiece;
  }

  public void setCapturedPiece(Piece capturedPiece) {
    this.capturedPiece = capturedPiece;
  }

  @Override
  public String toString() {
    char fromFile = (char) ('a' + fromCol);
    int fromRank = 8 - fromRow;
    char toFile = (char) ('a' + toCol);
    int toRank = 8 - toRow;
    return String.format("%s%c%d-%c%d", piece.getSymbol(), fromFile, fromRank, toFile, toRank);
  }
}
