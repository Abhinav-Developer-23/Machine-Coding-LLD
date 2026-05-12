package org.example.filesystem.composite;

import lombok.Getter;

@Getter
public class File extends FileSystemNode {
  private String content;

  public File(String name, Directory parent) {
    super(name, parent);
    this.content = "";
  }

    public void setContent(String content) {
    this.content = content;
  }
}
