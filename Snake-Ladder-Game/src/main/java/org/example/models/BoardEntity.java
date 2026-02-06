package org.example.models;

import lombok.Getter;

@Getter
public abstract class BoardEntity {
  private final int start;
  private final int end;

  public BoardEntity(int start, int end) {
    this.start = start;
    this.end = end;
  }
}
