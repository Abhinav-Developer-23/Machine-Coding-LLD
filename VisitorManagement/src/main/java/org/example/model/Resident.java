package org.example.model;

public class Resident {
  private final String id;
  private final String name;
  private final String houseId;

  public Resident(String id, String name, String houseId) {
    this.id = id;
    this.name = name;
    this.houseId = houseId;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getHouseId() {
    return houseId;
  }

  @Override
  public String toString() {
    return "Resident{id='%s', name='%s'}".formatted(id, name);
  }
}
