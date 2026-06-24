package versioncontrol.entities;

import lombok.Getter;

@Getter
public abstract class FileSystemNode {
  protected String name;

  protected FileSystemNode(String name) {
    this.name = name;
  }

  public abstract FileSystemNode clone();

  public abstract void print(String indent);
}
