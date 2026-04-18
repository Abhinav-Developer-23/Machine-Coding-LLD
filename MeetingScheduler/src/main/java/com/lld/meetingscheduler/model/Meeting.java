package com.lld.meetingscheduler.model;

import com.lld.meetingscheduler.enums.MeetingStatus;
import com.lld.meetingscheduler.exception.MeetingSchedulerException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

@Getter
public class Meeting {
  private final String id;
  private final String subject;
  private final User organizer;
  private final List<User> participants;
  private final Room room;
  private final TimeSlot timeSlot;
  private MeetingStatus status;

  public Meeting(
      String id,
      String subject,
      User organizer,
      List<User> participants,
      Room room,
      TimeSlot timeSlot) {
    this.id = id;
    this.subject = subject;
    this.organizer = organizer;
    this.participants = Collections.unmodifiableList(new ArrayList<>(participants));
    this.room = room;
    this.timeSlot = timeSlot;
    this.status = MeetingStatus.SCHEDULED;
  }

  public void cancel() {
    if (status != MeetingStatus.SCHEDULED) {
      throw new MeetingSchedulerException(
          "Can only cancel SCHEDULED meetings. Current status: " + status);
    }
    this.status = MeetingStatus.CANCELLED;
  }

  public void complete() {
    if (status != MeetingStatus.SCHEDULED) {
      throw new MeetingSchedulerException(
          "Can only complete SCHEDULED meetings. Current status: " + status);
    }
    this.status = MeetingStatus.COMPLETED;
  }

  @Override
  public String toString() {
    return "Meeting{id="
        + id
        + ", subject='"
        + subject
        + "', room="
        + room.getName()
        + ", time="
        + timeSlot
        + ", status="
        + status
        + "}";
  }
}
