package org.example;

import org.example.models.Board;

public class ChessDemo {
  public static void main(String[] args) {
    /*
     * Chess — interactive two-player game.
     *
     * Controls:
     *   - Enter moves in standard algebraic notation: <from> <to>
     *     e.g.  "e2 e4"  (move pawn from e2 to e4)
     *           "g1 f3"  (move knight from g1 to f3)
     *   - Type "resign" to forfeit.
     *
     * Board layout:
     *   - Uppercase letters = WHITE pieces
     *   - Lowercase letters = BLACK pieces
     *   - K/k=King  Q/q=Queen  R/r=Rook  B/b=Bishop  N/n=Knight  P/p=Pawn
     */
    Game game =
        new Game.Builder()
            .setBoard(new Board())
            .setWhitePlayer("Alice")
            .setBlackPlayer("Bob")
            .build();

    game.play();
  }
}
