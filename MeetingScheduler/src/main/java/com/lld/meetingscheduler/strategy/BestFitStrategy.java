package com.lld.meetingscheduler.strategy;

import java.util.List;

import com.lld.meetingscheduler.model.Room;

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
