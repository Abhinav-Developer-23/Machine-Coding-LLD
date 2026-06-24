package taskmanagement.entities;

import java.util.Date;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Tag {
  private final String name;

  public Tag(String name) {
    this.name = name;
  }
}
