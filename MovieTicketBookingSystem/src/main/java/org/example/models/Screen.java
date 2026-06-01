package org.example.models;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class Screen {
  private final String id;
  private final List<Seat> seats;

  public Screen(String id) {
    this.id = id;
    this.seats = new ArrayList<>();
  }

  public void addSeat(Seat seat) {
    seats.add(seat);
  }
}
