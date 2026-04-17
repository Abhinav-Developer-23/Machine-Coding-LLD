package com.lld.meetingscheduler.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.lld.meetingscheduler.model.Meeting;
import com.lld.meetingscheduler.model.Room;

public class MeetingSchedulerRepository {
    private final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Meeting> meetings = new ConcurrentHashMap<>();
    // room id -> active (non-cancelled) meetings in that room
    private final ConcurrentHashMap<String, List<Meeting>> roomMeetings = new ConcurrentHashMap<>();

    public void addRoom(Room room) {
        rooms.put(room.getId(), room);
        roomMeetings.putIfAbsent(room.getId(), new ArrayList<>());
    }

    public Collection<Room> getAllRooms() {
        return rooms.values();
    }

    public void saveMeeting(Meeting meeting) {
        meetings.put(meeting.getId(), meeting);
        roomMeetings.get(meeting.getRoom().getId()).add(meeting);
    }

    public Meeting getMeeting(String meetingId) {
        return meetings.get(meetingId);
    }

    public void removeRoomMeeting(Meeting meeting) {
        List<Meeting> list = roomMeetings.get(meeting.getRoom().getId());
        if (list != null) {
            list.remove(meeting);
        }
    }

    public List<Meeting> getMeetingsForRoom(String roomId) {
        List<Meeting> list = roomMeetings.get(roomId);
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }
}
