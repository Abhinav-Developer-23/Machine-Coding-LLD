package org.example.models;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class Theatre {
  private final int theatreId; // Unique identifier for the theatre
  private final String name; // Name of the theatre
  private final List<Screen> screen; // List of screens available in the theatre

  public Theatre(final int theatreId, final String name) {
    this.theatreId = theatreId;
    this.name = name;
    this.screen = new ArrayList<>();
  }

  public void addScreen(final Screen screen) {
    this.screen.add(screen);
  }
}
