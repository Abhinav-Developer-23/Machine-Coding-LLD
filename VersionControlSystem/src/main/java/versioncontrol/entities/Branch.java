package versioncontrol.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Branch {
  private String name;
  private Commit head;

  public Branch(String name, Commit head) {
    this.name = name;
    this.head = head;
  }
}
