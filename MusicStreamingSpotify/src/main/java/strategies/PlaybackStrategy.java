package strategies;

import entities.Song;
import enums.SubscriptionTier;
import player.Player;

public interface PlaybackStrategy {
  void play(Song song, Player player);

  static PlaybackStrategy getStrategy(SubscriptionTier tier, int songsPlayed) {
    return tier == SubscriptionTier.PREMIUM
        ? new PremiumPlaybackStrategy()
        : new FreePlaybackStrategy(songsPlayed);
  }
}
