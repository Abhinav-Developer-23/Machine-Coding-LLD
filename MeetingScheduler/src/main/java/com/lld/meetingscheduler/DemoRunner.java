package com.lld.meetingscheduler;

import com.lld.meetingscheduler.enums.RoomType;
import com.lld.meetingscheduler.exception.MeetingSchedulerException;
import com.lld.meetingscheduler.model.Meeting;
import com.lld.meetingscheduler.model.Room;
import com.lld.meetingscheduler.model.TimeSlot;
import com.lld.meetingscheduler.model.User;
import com.lld.meetingscheduler.observer.CalendarNotificationObserver;
import com.lld.meetingscheduler.observer.EmailNotificationObserver;
import com.lld.meetingscheduler.repository.MeetingSchedulerRepository;
import com.lld.meetingscheduler.service.MeetingSchedulerService;
import com.lld.meetingscheduler.strategy.BestFitStrategy;
import com.lld.meetingscheduler.strategy.FirstAvailableStrategy;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class DemoRunner {
  public static void main(String[] args) {
    MeetingSchedulerRepository repository = new MeetingSchedulerRepository();
    MeetingSchedulerService scheduler =
        new MeetingSchedulerService(repository, new FirstAvailableStrategy());

    scheduler.addObserver(new EmailNotificationObserver());
    scheduler.addObserver(new CalendarNotificationObserver());

    Room everest = new Room("R1", "Everest", RoomType.CONFERENCE, 10);
    Room alps = new Room("R2", "Alps", RoomType.BOARD_ROOM, 20);
    Room nook = new Room("R3", "Nook", RoomType.HUDDLE_SPACE, 4);
    scheduler.addRoom(everest);
    scheduler.addRoom(alps);
    scheduler.addRoom(nook);

    User alice = new User("U1", "Alice", "alice@example.com");
    User bob = new User("U2", "Bob", "bob@example.com");
    User charlie = new User("U3", "Charlie", "charlie@example.com");
    User diana = new User("U4", "Diana", "diana@example.com");

    LocalDateTime now = LocalDateTime.of(2025, 1, 15, 9, 0);

    System.out.println("========== SCENARIO 1: Schedule Meeting (First Available) ==========");
    TimeSlot slot1 = new TimeSlot(now, now.plusHours(1));
    Meeting meeting1 =
        scheduler.scheduleMeeting("Sprint Planning", alice, Arrays.asList(bob, charlie), slot1, 3);
    System.out.println("Scheduled: " + meeting1);

    System.out.println(
        "\n========== SCENARIO 2: Overlapping Slot (Another Room Available) ==========");
    TimeSlot slot2 = new TimeSlot(now.plusMinutes(30), now.plusHours(2));
    Meeting meeting2 =
        scheduler.scheduleMeeting("Design Review", bob, Arrays.asList(alice), slot2, 3);
    System.out.println("Scheduled: " + meeting2);

    System.out.println("\n========== SCENARIO 3: Schedule Meeting (Best Fit) ==========");
    scheduler.setRoomSelectionStrategy(new BestFitStrategy());
    TimeSlot slot3 = new TimeSlot(now.plusHours(2), now.plusHours(3));
    Meeting meeting3 =
        scheduler.scheduleMeeting("1-on-1 Sync", alice, Arrays.asList(diana), slot3, 2);
    System.out.println("Scheduled: " + meeting3);

    System.out.println("\n========== SCENARIO 4: Cancel Meeting ==========");
    scheduler.cancelMeeting(meeting1.getId());
    System.out.println(
        "Meeting cancelled: " + meeting1.getSubject() + " (status=" + meeting1.getStatus() + ")");

    System.out.println("\n========== SCENARIO 5: Check Available Rooms ==========");
    List<Room> available = scheduler.getAvailableRooms(slot1, 2);
    System.out.println("Available rooms for " + slot1 + ":");
    for (Room room : available) {
      System.out.println("  - " + room);
    }

    System.out.println("\n========== SCENARIO 6: Schedule in Freed Slot ==========");
    Meeting meeting4 =
        scheduler.scheduleMeeting(
            "Retrospective", charlie, Arrays.asList(alice, bob, diana), slot1, 5);
    System.out.println("Scheduled: " + meeting4);

    System.out.println("\n========== EDGE CASE: No Room Fits Capacity ==========");
    try {
      TimeSlot slot4 = new TimeSlot(now.plusHours(4), now.plusHours(5));
      scheduler.scheduleMeeting("All Hands", alice, Arrays.asList(bob, charlie, diana), slot4, 100);
    } catch (MeetingSchedulerException e) {
      System.out.println("Rejected as expected: " + e.getMessage());
    }
  }
}
