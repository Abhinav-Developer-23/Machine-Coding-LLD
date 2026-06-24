package player.states;

import player.Player;
import player.PlayerState;

public class PlayingState implements PlayerState {
  @Override
  public void play(Player player) {
    System.out.println("Already playing.");
  }

  @Override
  public void next(Player player) {
    player.advanceToNextSong();
  }

  @Override
  public void pause(Player player) {
    System.out.println("Pausing playback.");
    player.changeState(new PausedState());
  }

  @Override
  public void stop(Player player) {
    System.out.println("Stopping playback.");
    player.changeState(new StoppedState());
  }
}
