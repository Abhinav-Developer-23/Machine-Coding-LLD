package com.lld.meetingscheduler.strategy;

import java.util.List;

import com.lld.meetingscheduler.model.Room;

public interface RoomSelectionStrategy {
    Room selectRoom(List<Room> availableRooms, int requiredCapacity);
}
