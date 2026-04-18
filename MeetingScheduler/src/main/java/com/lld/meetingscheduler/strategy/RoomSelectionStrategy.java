package com.lld.meetingscheduler.strategy;

import com.lld.meetingscheduler.model.Room;
import java.util.List;

public interface RoomSelectionStrategy {
  Room selectRoom(List<Room> availableRooms, int requiredCapacity);
}
