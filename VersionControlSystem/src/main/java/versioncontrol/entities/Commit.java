package versioncontrol.entities;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Commit {
  private final String id;
  private final String message;
  private final String author;
  private final LocalDateTime timestamp;
  private final Commit parent;
  private final Directory rootSnapshot;

  public Commit(String author, String message, Commit parent, Directory rootSnapshot) {
    this.id = UUID.randomUUID().toString().substring(0, 8);
    this.author = author;
    this.message = message;
    this.parent = parent;
    this.rootSnapshot = rootSnapshot;
    this.timestamp = LocalDateTime.now();
  }
}
