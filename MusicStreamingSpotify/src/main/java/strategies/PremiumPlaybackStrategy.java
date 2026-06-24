package strategies;

import entities.Song;
import player.Player;

public class PremiumPlaybackStrategy implements PlaybackStrategy {
  @Override
  public void play(Song song, Player player) {
    System.out.printf("Premium User is now playing: %s%n", song);
  }
}
