package org.example.models;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class Screen {
  private final int screenId; // Unique identifier for the screen
  private final String name; // Name of the screen
  private final Theatre theatre; // The theater to which this screen belongs
  private final List<Seat> seats; // List of seats available in this screen

  public Screen(final int screenId, final String name, final Theatre theatre) {
    this.screenId = screenId;
    this.name = name;
    this.theatre = theatre;
    this.seats = new ArrayList<>();
  }

  public void addSeat(final Seat seat) {
    this.seats.add(seat);
  }
}
