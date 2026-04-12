package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.example.enums.GameStatus;
import org.example.enums.Symbol;
import org.example.exceptions.InvalidMoveException;
import org.example.models.Board;
import org.example.models.Player;
import org.example.observers.GameObserver;
import org.example.strategies.ColumnWinningStrategy;
import org.example.strategies.DiagonalWinningStrategy;
import org.example.strategies.RowWinningStrategy;
import org.example.strategies.WinningStrategy;

public class Game {
    private final Board board;
    private final Player[] players;
    private int currentPlayerIndex;
    private GameStatus status;
    private final List<WinningStrategy> winningStrategies;
    private final List<GameObserver> observers;

    public Game(Player player1, Player player2, int boardSize) {
        this.board = new Board(boardSize);
        this.players = new Player[] {player1, player2};
        this.currentPlayerIndex = 0;
        this.status = GameStatus.IN_PROGRESS;
        this.winningStrategies = initializeStrategies();
        this.observers = new CopyOnWriteArrayList<>();
    }

    private List<WinningStrategy> initializeStrategies() {
        List<WinningStrategy> strategies = new ArrayList<>();
        strategies.add(new RowWinningStrategy());
        strategies.add(new ColumnWinningStrategy());
        strategies.add(new DiagonalWinningStrategy());
        return strategies;
    }

    public synchronized void makeMove(int row, int col) {
        if (status != GameStatus.IN_PROGRESS) {
            throw new InvalidMoveException("Game is already over!");
        }

        if (!board.isCellEmpty(row, col)) {
            throw new InvalidMoveException("Cell (" + row + ", " + col + ") is already occupied");
        }

        Player currentPlayer = players[currentPlayerIndex];
        board.placeSymbol(row, col, currentPlayer.getSymbol());

        if (checkWin(row, col, currentPlayer.getSymbol())) {
            status =
                    (currentPlayer.getSymbol() == Symbol.X)
                            ? GameStatus.WINNER_X
                            : GameStatus.WINNER_O;
            notifyObservers();
            return;
        }

        if (board.isFull()) {
            status = GameStatus.DRAW;
            notifyObservers();
            return;
        }

        currentPlayerIndex = (currentPlayerIndex + 1) % 2;
    }

    private boolean checkWin(int row, int col, Symbol symbol) {
        for (WinningStrategy strategy : winningStrategies) {
            if (strategy.checkWin(board, row, col, symbol)) {
                return true;
            }
        }
        return false;
    }

    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers() {
        for (GameObserver observer : observers) {
            observer.update(this);
        }
    }

    public Board getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return players[currentPlayerIndex];
    }

    public GameStatus getStatus() {
        return status;
    }

    public Player getWinner() {
        if (status == GameStatus.WINNER_X) {
            return players[0].getSymbol() == Symbol.X ? players[0] : players[1];
        } else if (status == GameStatus.WINNER_O) {
            return players[0].getSymbol() == Symbol.O ? players[0] : players[1];
        }
        return null;
    }

    public void printBoard() {
        board.printBoard();
    }
}
