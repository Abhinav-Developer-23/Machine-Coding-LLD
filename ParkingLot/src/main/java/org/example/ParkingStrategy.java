package org.example;

import java.util.List;
import java.util.Optional;

public interface ParkingStrategy {
  Optional<ParkingSpot> findSpot(List<ParkingFloor> floors, Vehicle vehicle);
}
