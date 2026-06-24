package player.states;

import player.Player;
import player.PlayerState;

public class StoppedState implements PlayerState {
  @Override
  public void play(Player player) {
    if (player.hasQueue()) {
      System.out.println("Starting playback.");
      player.changeState(new PlayingState());
      player.playCurrentSongInQueue();
    } else {
      System.out.println("Queue is empty. Load songs to play.");
    }
  }

  @Override
  public void pause(Player player) {
    System.out.println("Cannot pause. Player is stopped.");
  }

  @Override
  public void next(Player player) {
    System.out.println("Cannot skip. Player is stopped.");
  }

  @Override
  public void stop(Player player) {
    System.out.println("Already stopped.");
  }
}
