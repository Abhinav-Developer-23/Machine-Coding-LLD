package org.example.models;

import org.example.enums.Color;

public class Player {
  private final String name;
  private final Color color;

  public Player(String name, Color color) {
    this.name = name;
    this.color = color;
  }

  public String getName() {
    return name;
  }

  public Color getColor() {
    return color;
  }

  @Override
  public String toString() {
    return String.format("%s (%s)", name, color);
  }
}
