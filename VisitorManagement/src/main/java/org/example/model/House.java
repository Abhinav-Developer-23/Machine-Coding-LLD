package org.example.model;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class House {
  private final String id;
  private final String houseNumber;
  private final String societyId;
  private final Map<String, Resident> residentsById;

  public House(String id, String houseNumber, String societyId) {
    this.id = id;
    this.houseNumber = houseNumber;
    this.societyId = societyId;
    this.residentsById = new LinkedHashMap<>();
  }

  public String getId() {
    return id;
  }

  public String getHouseNumber() {
    return houseNumber;
  }

  public String getSocietyId() {
    return societyId;
  }

  public void addResident(Resident resident) {
    residentsById.put(resident.getId(), resident);
  }

  public boolean hasResident(String residentId) {
    return residentsById.containsKey(residentId);
  }

  public Collection<Resident> getResidents() {
    return residentsById.values();
  }

  @Override
  public String toString() {
    return "House{id='%s', houseNumber='%s', residents=%d}"
        .formatted(id, houseNumber, residentsById.size());
  }
}
