package versioncontrol.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class File extends FileSystemNode {
  private String content;

  public File(String name, String content) {
    super(name);
    this.content = content;
  }

  @Override
  public FileSystemNode clone() {
    return new File(this.name, this.content);
  }

  @Override
  public void print(String indent) {
    System.out.println(indent + "- " + name + " (File)");
  }
}
