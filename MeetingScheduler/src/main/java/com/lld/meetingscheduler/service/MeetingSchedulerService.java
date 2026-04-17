package com.lld.meetingscheduler.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.lld.meetingscheduler.exception.MeetingSchedulerException;
import com.lld.meetingscheduler.model.Meeting;
import com.lld.meetingscheduler.model.Room;
import com.lld.meetingscheduler.model.TimeSlot;
import com.lld.meetingscheduler.model.User;
import com.lld.meetingscheduler.observer.MeetingObserver;
import com.lld.meetingscheduler.repository.MeetingSchedulerRepository;
import com.lld.meetingscheduler.strategy.RoomSelectionStrategy;

public class MeetingSchedulerService {
    private final MeetingSchedulerRepository repository;
    private final CopyOnWriteArrayList<MeetingObserver> observers = new CopyOnWriteArrayList<>();
    private final AtomicInteger meetingCounter = new AtomicInteger(0);
    private RoomSelectionStrategy roomSelectionStrategy;

    public MeetingSchedulerService(MeetingSchedulerRepository repository,
                                   RoomSelectionStrategy roomSelectionStrategy) {
        this.repository = repository;
        this.roomSelectionStrategy = roomSelectionStrategy;
    }

    public void addRoom(Room room) {
        repository.addRoom(room);
    }

    public synchronized Meeting scheduleMeeting(String subject, User organizer,
                                                List<User> participants,
                                                TimeSlot timeSlot,
                                                int requiredCapacity) {
        List<Room> availableRooms = getAvailableRooms(timeSlot, requiredCapacity);
        Room selectedRoom = roomSelectionStrategy.selectRoom(availableRooms, requiredCapacity);

        if (selectedRoom == null) {
            throw new MeetingSchedulerException(
                    "No available room found for capacity " + requiredCapacity
                            + " during " + timeSlot);
        }

        String meetingId = "MTG-" + meetingCounter.incrementAndGet();
        Meeting meeting = new Meeting(meetingId, subject, organizer,
                participants, selectedRoom, timeSlot);
        repository.saveMeeting(meeting);

        notifyMeetingScheduled(meeting);
        return meeting;
    }

    public synchronized void cancelMeeting(String meetingId) {
        Meeting meeting = repository.getMeeting(meetingId);
        if (meeting == null) {
            throw new MeetingSchedulerException("Meeting not found: " + meetingId);
        }
        meeting.cancel();
        repository.removeRoomMeeting(meeting);
        notifyMeetingCancelled(meeting);
    }

    public synchronized List<Room> getAvailableRooms(TimeSlot timeSlot, int requiredCapacity) {
        List<Room> available = new ArrayList<>();
        for (Room room : repository.getAllRooms()) {
            if (room.getCapacity() < requiredCapacity) {
                continue;
            }
            if (hasConflict(room, timeSlot)) {
                continue;
            }
            available.add(room);
        }
        return available;
    }

    private boolean hasConflict(Room room, TimeSlot timeSlot) {
        List<Meeting> existing = repository.getMeetingsForRoom(room.getId());
        for (Meeting m : existing) {
            if (m.getTimeSlot().overlaps(timeSlot)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void setRoomSelectionStrategy(RoomSelectionStrategy strategy) {
        this.roomSelectionStrategy = strategy;
    }

    public void addObserver(MeetingObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(MeetingObserver observer) {
        observers.remove(observer);
    }

    private void notifyMeetingScheduled(Meeting meeting) {
        for (MeetingObserver observer : observers) {
            try {
                observer.onMeetingScheduled(meeting);
            } catch (Exception e) {
                System.err.println("Observer notification failed: " + e.getMessage());
            }
        }
    }

    private void notifyMeetingCancelled(Meeting meeting) {
        for (MeetingObserver observer : observers) {
            try {
                observer.onMeetingCancelled(meeting);
            } catch (Exception e) {
                System.err.println("Observer notification failed: " + e.getMessage());
            }
        }
    }
}
