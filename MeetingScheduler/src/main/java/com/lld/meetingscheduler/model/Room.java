package com.lld.meetingscheduler.model;

import com.lld.meetingscheduler.enums.RoomType;
import lombok.Getter;

@Getter
public class Room {
  private final String id;
  private final String name;
  private final RoomType roomType;
  private final int capacity;

  public Room(String id, String name, RoomType roomType, int capacity) {
    this.id = id;
    this.name = name;
    this.roomType = roomType;
    this.capacity = capacity;
  }

  @Override
  public String toString() {
    return name + " (" + roomType + ", capacity: " + capacity + ")";
  }
}
