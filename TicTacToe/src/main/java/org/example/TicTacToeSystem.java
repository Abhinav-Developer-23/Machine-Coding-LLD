package org.example;

import org.example.enums.GameStatus;
import org.example.models.Player;
import org.example.observers.Scoreboard;

public class TicTacToeSystem {
    private static volatile TicTacToeSystem instance;
    private static final Object lock = new Object();

    private final Scoreboard scoreboard;
    private Game currentGame;

    private TicTacToeSystem() {
        this.scoreboard = new Scoreboard();
    }

    public static TicTacToeSystem getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new TicTacToeSystem();
                }
            }
        }
        return instance;
    }

    public Game createGame(Player player1, Player player2) {
        currentGame = new Game(player1, player2, 3);
        currentGame.addObserver(scoreboard);
        System.out.println("New game started: " + player1.getName() + " vs " + player2.getName());
        return currentGame;
    }

    public void makeMove(Player player, int row, int col) {
        if (currentGame == null) {
            throw new IllegalStateException("No active game. Call createGame first.");
        }
        System.out.println(player.getName() + " plays at (" + row + ", " + col + ")");
        currentGame.makeMove(row, col);
        currentGame.printBoard();
    }

    public GameStatus getGameStatus() {
        if (currentGame == null) {
            throw new IllegalStateException("No active game.");
        }
        return currentGame.getStatus();
    }

    public void printScoreboard() {
        scoreboard.printScoreboard();
    }

    public static void resetInstance() {
        synchronized (lock) {
            instance = null;
        }
    }
}
