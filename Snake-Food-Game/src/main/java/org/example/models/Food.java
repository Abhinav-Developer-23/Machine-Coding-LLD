package org.example.models;

import lombok.Getter;

@Getter
public final class Food {
  private final String id;
  private final Position position;

  public Food(String id, Position position) {
    this.id = id;
    this.position = position;
  }
}
