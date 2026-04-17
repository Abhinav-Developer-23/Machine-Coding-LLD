package com.lld.meetingscheduler.strategy;

import java.util.List;

import com.lld.meetingscheduler.model.Room;

public class FirstAvailableStrategy implements RoomSelectionStrategy {
    @Override
    public Room selectRoom(List<Room> availableRooms, int requiredCapacity) {
        if (availableRooms.isEmpty()) {
            return null;
        }
        return availableRooms.get(0);
    }
}
