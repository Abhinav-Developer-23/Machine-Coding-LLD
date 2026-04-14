package org.example;

import java.util.Scanner;
import org.example.enums.Color;
import org.example.enums.GameStatus;
import org.example.exceptions.InvalidMoveException;
import org.example.models.Board;
import org.example.models.Move;
import org.example.models.Player;
import org.example.pieces.Piece;

public class Game {
  private final Board board;
  private final Player whitePlayer;
  private final Player blackPlayer;
  private Player currentPlayer;
  private Player winner;
  private GameStatus status;
  private int totalMoves;

  private Game(Builder builder) {
    this.board = builder.board;
    this.whitePlayer = builder.whitePlayer;
    this.blackPlayer = builder.blackPlayer;
    this.currentPlayer = whitePlayer; // White always goes first
    this.winner = null;
    this.status = GameStatus.NOT_STARTED;
    this.totalMoves = 0;
  }

  public void play() {
    this.status = GameStatus.RUNNING;
    System.out.printf("%nChess game started: %s vs %s%n", whitePlayer, blackPlayer);
    board.display();

    try (Scanner scanner = new Scanner(System.in)) {
      while (status == GameStatus.RUNNING || status == GameStatus.CHECK) {
        System.out.printf("%s's turn", currentPlayer.getName());
        if (status == GameStatus.CHECK) {
          System.out.print(" [CHECK!]");
        }
        System.out.print(" — enter move (e.g. e2 e4), or 'resign': ");

        String input = scanner.nextLine().trim();

        if (input.equalsIgnoreCase("resign")) {
          handleResign();
          break;
        }

        try {
          Move move = parseAndValidateMove(input);
          executeMove(move);
          updateGameStatus();
          board.display();
          printStatusMessage(move);
          if (status == GameStatus.RUNNING || status == GameStatus.CHECK) {
            switchTurn();
          }
        } catch (InvalidMoveException e) {
          System.out.println("Invalid move: " + e.getMessage() + ". Try again.");
        }
      }
    }

    announceResult();
  }

  private Move parseAndValidateMove(String input) {
    String[] parts = input.trim().split("\\s+");
    if (parts.length != 2) {
      throw new InvalidMoveException("Use format like 'e2 e4'");
    }

    int[] from = parseSquare(parts[0]);
    int[] to = parseSquare(parts[1]);

    int fromRow = from[0], fromCol = from[1];
    int toRow = to[0], toCol = to[1];

    Piece piece = board.getCell(fromRow, fromCol).getPiece();
    if (piece == null) {
      throw new InvalidMoveException("No piece at " + parts[0]);
    }
    if (piece.getColor() != currentPlayer.getColor()) {
      throw new InvalidMoveException("That piece belongs to your opponent");
    }

    Move move = new Move(fromRow, fromCol, toRow, toCol, piece);
    boolean isLegal =
        piece.getValidMoves(board, fromRow, fromCol).stream()
            .anyMatch(m -> m.getToRow() == toRow && m.getToCol() == toCol);

    if (!isLegal) {
      throw new InvalidMoveException(
          piece.getSymbol() + " cannot move from " + parts[0] + " to " + parts[1]);
    }

    return move;
  }

  /** Parses a square string like "e2" into [row, col]. */
  private int[] parseSquare(String square) {
    if (square.length() != 2) {
      throw new InvalidMoveException("Square '" + square + "' is invalid. Use format like 'e2'");
    }
    char file = Character.toLowerCase(square.charAt(0));
    char rank = square.charAt(1);

    if (file < 'a' || file > 'h' || rank < '1' || rank > '8') {
      throw new InvalidMoveException("Square '" + square + "' is out of bounds");
    }

    int col = file - 'a';
    int row = 8 - (rank - '0');
    return new int[] {row, col};
  }

  private void executeMove(Move move) {
    board.applyMove(move);
    totalMoves++;
  }

  private void updateGameStatus() {
    Color opponentColor = currentPlayer.getColor().opponent();
    boolean opponentInCheck = board.isInCheck(opponentColor);
    boolean opponentHasNoMoves = board.hasNoLegalMoves(opponentColor);

    if (opponentInCheck && opponentHasNoMoves) {
      status = GameStatus.CHECKMATE;
      winner = currentPlayer;
    } else if (!opponentInCheck && opponentHasNoMoves) {
      status = GameStatus.STALEMATE;
    } else if (opponentInCheck) {
      status = GameStatus.CHECK;
    } else {
      status = GameStatus.RUNNING;
    }
  }

  private void printStatusMessage(Move move) {
    System.out.printf("Move %d: %s played %s%n", totalMoves, currentPlayer.getName(), move);
    if (status == GameStatus.CHECK) {
      Player opponent = (currentPlayer == whitePlayer) ? blackPlayer : whitePlayer;
      System.out.printf("%s is in CHECK!%n", opponent.getName());
    }
  }

  private void switchTurn() {
    currentPlayer = (currentPlayer == whitePlayer) ? blackPlayer : whitePlayer;
  }

  private void handleResign() {
    winner = (currentPlayer == whitePlayer) ? blackPlayer : whitePlayer;
    System.out.printf("%s resigned. %s wins!%n", currentPlayer.getName(), winner.getName());
    status = GameStatus.CHECKMATE;
  }

  private void announceResult() {
    System.out.println();
    System.out.println("=== Game Over ===");
    System.out.printf("Total moves played: %d%n", totalMoves);
    switch (status) {
      case CHECKMATE -> System.out.printf("CHECKMATE! %s wins!%n", winner.getName());
      case STALEMATE -> System.out.println("STALEMATE! It's a draw.");
      case DRAW -> System.out.println("DRAW!");
      default -> System.out.println("Game ended.");
    }
  }

  public GameStatus getStatus() {
    return status;
  }

  public int getTotalMoves() {
    return totalMoves;
  }

  // Builder class — mirrors the Snake and Ladder Builder pattern
  public static class Builder {
    private Board board;
    private Player whitePlayer;
    private Player blackPlayer;

    public Builder setBoard(Board board) {
      this.board = board;
      return this;
    }

    public Builder setWhitePlayer(String name) {
      this.whitePlayer = new Player(name, Color.WHITE);
      return this;
    }

    public Builder setBlackPlayer(String name) {
      this.blackPlayer = new Player(name, Color.BLACK);
      return this;
    }

    public Game build() {
      if (board == null || whitePlayer == null || blackPlayer == null) {
        throw new IllegalStateException("Board, white player, and black player must all be set.");
      }
      return new Game(this);
    }
  }
}
