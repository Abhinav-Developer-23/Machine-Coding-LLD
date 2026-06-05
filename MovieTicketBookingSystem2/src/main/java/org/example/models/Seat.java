package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.enums.SeatCategory;

@Getter
@AllArgsConstructor
public class Seat {
  private final int seatId; // Unique identifier for the seat
  private final int row; // Row number where the seat is located
  private final SeatCategory seatCategory; // Category of the seat (e.g., Silver, Gold, Platinum)
}
