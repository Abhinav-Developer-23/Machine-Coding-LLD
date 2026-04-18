package com.lld.meetingscheduler.strategy;

import com.lld.meetingscheduler.model.Room;
import java.util.List;

public class FirstAvailableStrategy implements RoomSelectionStrategy {
  @Override
  public Room selectRoom(List<Room> availableRooms, int requiredCapacity) {
    if (availableRooms.isEmpty()) {
      return null;
    }
    return availableRooms.get(0);
  }
}
