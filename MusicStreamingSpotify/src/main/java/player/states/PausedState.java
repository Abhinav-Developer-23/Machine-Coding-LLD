package player.states;

import player.Player;
import player.PlayerState;

public class PausedState implements PlayerState {
  @Override
  public void play(Player player) {
    System.out.println("Resuming playback.");
    player.changeState(new PlayingState());
  }

  @Override
  public void pause(Player player) {
    System.out.println("Already paused.");
  }

  @Override
  public void next(Player player) {
    player.changeState(new PlayingState());
    player.advanceToNextSong();
  }

  @Override
  public void stop(Player player) {
    System.out.println("Stopping playback from paused state.");
    player.changeState(new StoppedState());
  }
}
