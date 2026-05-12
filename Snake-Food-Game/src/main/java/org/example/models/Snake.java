package org.example.models;

import lombok.Getter;

@Getter
public final class Snake {
  private final String id;

  public Snake(String id) {
    this.id = id;
  }
}
