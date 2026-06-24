package entities;

import java.util.ArrayList;
import java.util.List;

import interfaces.Playable;
import lombok.Getter;

@Getter
public class Album implements Playable {
  private final String title;
  private final Artist artist;
  private final List<Song> tracks = new ArrayList<>();

  public Album(String title, Artist artist) {
    this.title = title;
    this.artist = artist;
  }

  public void addTrack(Song song) {
    tracks.add(song);
  }

  @Override
  public List<Song> getTracks() {
    return List.copyOf(tracks);
  }
}
