package org.example.model;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Society {
  private final String id;
  private final String name;
  private final Map<String, House> housesByNumber;

  public Society(String id, String name) {
    this.id = id;
    this.name = name;
    this.housesByNumber = new LinkedHashMap<>();
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void addHouse(House house) {
    housesByNumber.put(house.getHouseNumber(), house);
  }

  public boolean hasHouse(String houseNumber) {
    return housesByNumber.containsKey(houseNumber);
  }

  public House getHouseByNumber(String houseNumber) {
    return housesByNumber.get(houseNumber);
  }

  public Collection<House> getHouses() {
    return housesByNumber.values();
  }

  @Override
  public String toString() {
    return "Society{id='%s', name='%s', houses=%d}".formatted(id, name, housesByNumber.size());
  }
}
