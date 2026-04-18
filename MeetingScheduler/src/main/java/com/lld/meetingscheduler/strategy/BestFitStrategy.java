package com.lld.meetingscheduler.strategy;

import com.lld.meetingscheduler.model.Room;
import java.util.List;

public class BestFitStrategy implements RoomSelectionStrategy {
  @Override
  public Room selectRoom(List<Room> availableRooms, int requiredCapacity) {
    Room best = null;
    for (Room room : availableRooms) {
      if (best == null || room.getCapacity() < best.getCapacity()) {
        best = room;
      }
    }
    return best;
  }
}
