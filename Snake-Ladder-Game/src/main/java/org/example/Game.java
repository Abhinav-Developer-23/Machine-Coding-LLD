package org.example;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import org.example.enums.GameStatus;
import org.example.models.Board;
import org.example.models.BoardEntity;
import org.example.models.Dice;
import org.example.models.Player;

public class Game {
  private final Board board;
  private final Deque<Player> players;
  private final Dice dice;
  private GameStatus status;
  private Player winner;

  /**
   * Private constructor that initializes the game using a Builder instance. Copies the board and
   * dice references from the builder, creates a new ArrayDeque from the builder's player deque (to
   * avoid sharing mutable state), and sets the initial game status to NOT_STARTED.
   *
   * @param builder the builder containing the configured board, players, and dice
   */
  private Game(Builder builder) {
    this.board = builder.board;
    this.players = new LinkedList<>(builder.players);
    this.dice = builder.dice;
    this.status = GameStatus.NOT_STARTED;
  }

  /**
   * Starts and runs the game loop until a player wins.
   *
   * <p>1. Validates that at least 2 players are present; exits early if not. 2. Sets the game
   * status to RUNNING. 3. Enters the main game loop: in each iteration, the next player is removed
   * from the front of the deque via {@code pollFirst()} and {@code takeTurn} is called. {@code
   * takeTurn} handles movement, win detection, and re-queuing the player (front on a 6, back
   * otherwise). 4. Once a player wins (status becomes FINISHED), the loop exits and the winner is
   * announced.
   */
  public void play() {
    if (players.size() < 2) {
      System.out.println("Cannot start game. At least 2 players are required.");
      return;
    }

    this.status = GameStatus.RUNNING;
    System.out.println("Game started!");

    while (status == GameStatus.RUNNING) {
      Player currentPlayer = players.poll();
      takeTurn(currentPlayer);
    }

    System.out.println("Game Finished!");
    if (winner != null) {
      System.out.printf("The winner is %s!\n", winner.getName());
    }
  }

  /**
   * Executes a single turn for the given player, then re-queues them in the deque.
   *
   * <p>1. Rolls the dice to get a random value. 2. Computes the tentative next position (current +
   * roll). 3. Overshoot check: if the next position exceeds the board size, the player cannot move
   * and the turn is skipped (must land exactly on the last square). 4. Win check: if the next
   * position equals the board size exactly, the player wins -- their position is updated, the
   * winner is recorded, the game status is set to FINISHED, and the method returns without
   * re-queuing. 5. Snake/Ladder resolution: the board is queried for a snake or ladder at the
   * landing square. If a ladder exists (final > next), the player climbs up. If a snake exists
   * (final < next), the player slides down. Otherwise, the player stays at the landed position. 6.
   * Updates the player's position to the resolved final position. 7. Re-queues the player: if the
   * roll was a 6, adds to the front of the deque via {@code addFirst()} for a bonus turn; otherwise
   * adds to the back via {@code addLast()} for normal rotation.
   *
   * @param player the player whose turn it is
   */
  private void takeTurn(Player player) {
    int roll = dice.roll();
    System.out.printf("\n%s's turn. Rolled a %d.\n", player.getName(), roll);

    int currentPosition = player.getPosition();
    int nextPosition = currentPosition + roll;

    if (nextPosition > board.getSize()) {
      System.out.printf(
          "Oops, %s needs to land exactly on %d. Turn skipped.\n",
          player.getName(), board.getSize());
    } else if (nextPosition == board.getSize()) {
      player.setPosition(nextPosition);
      this.winner = player;
      this.status = GameStatus.FINISHED;
      System.out.printf(
          "Hooray! %s reached the final square %d and won!\n", player.getName(), board.getSize());
      return;
    } else {
      int finalPosition = board.getFinalPosition(nextPosition);

      if (finalPosition > nextPosition) { // Ladder
        System.out.printf(
            "Wow! %s found a ladder at %d and climbed to %d.\n",
            player.getName(), nextPosition, finalPosition);
      } else if (finalPosition < nextPosition) { // Snake
        System.out.printf(
            "Oh no! %s was bitten by a snake at %d and slid down to %d.\n",
            player.getName(), nextPosition, finalPosition);
      } else {
        System.out.printf(
            "%s moved from %d to %d.\n", player.getName(), currentPosition, finalPosition);
      }

      player.setPosition(finalPosition);
    }

    // Re-queue the player: bonus turn (front) on a 6, normal rotation (back) otherwise
    if (roll == 6) {
      System.out.printf("%s rolled a 6 and gets another turn!\n", player.getName());
      players.addFirst(player);
    } else {
      players.addLast(player);
    }
  }

  /**
   * Builder for constructing a {@link Game} instance step by step. Ensures all required components
   * (board, players, dice) are set before the game can be built.
   */
  public static class Builder {
    private Board board;
    private Deque<Player> players;
    private Dice dice;

    /**
     * Creates the game board with the given size and list of board entities (snakes and ladders).
     * Internally constructs a {@link Board} that maps each entity's start position to its end
     * position.
     *
     * @param boardSize the total number of squares on the board (e.g. 100)
     * @param boardEntities the list of snakes and ladders to place on the board
     * @return this builder for method chaining
     */
    public Builder setBoard(int boardSize, List<BoardEntity> boardEntities) {
      this.board = new Board(boardSize, boardEntities);
      return this;
    }

    /**
     * Creates players from the given list of names. Each name is used to instantiate a new {@link
     * Player} (starting at position 0), and all players are added to an ArrayDeque that determines
     * turn order.
     *
     * @param playerNames the names of the players, in turn order
     * @return this builder for method chaining
     */
    public Builder setPlayers(List<String> playerNames) {
      this.players = new LinkedList<>();
      for (String playerName : playerNames) {
        players.add(new Player(playerName));
      }
      return this;
    }

    /**
     * Sets the dice to be used throughout the game for rolling.
     *
     * @param dice the dice instance with configured min and max values
     * @return this builder for method chaining
     */
    public Builder setDice(Dice dice) {
      this.dice = dice;
      return this;
    }

    /**
     * Validates that all required components have been set and constructs the {@link Game}
     * instance. Throws an {@link IllegalStateException} if any of board, players, or dice is
     * missing.
     *
     * @return a fully configured Game ready to play
     * @throws IllegalStateException if board, players, or dice is not set
     */
    public Game build() {
      if (board == null || players == null || dice == null) {
        throw new IllegalStateException("Board, Players, and Dice must be set.");
      }
      return new Game(this);
    }
  }
}
