package stackoverflow.entities;

import lombok.Getter;

@Getter
public class Tag {
  private final String name;

  public Tag(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Tag)) {
      return false;
    }
    return name.equalsIgnoreCase(((Tag) o).name);
  }

  @Override
  public int hashCode() {
    return name.toLowerCase().hashCode();
  }
}
