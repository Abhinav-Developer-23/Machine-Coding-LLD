package interfaces;

import entities.Album;
import entities.Artist;

public interface ArtistObserver {
  void update(Artist artist, Album newAlbum);
}
