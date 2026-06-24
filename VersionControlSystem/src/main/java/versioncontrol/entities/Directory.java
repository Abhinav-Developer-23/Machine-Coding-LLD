package versioncontrol.entities;

import java.util.Map;
import java.util.TreeMap;
import lombok.Getter;

@Getter
public class Directory extends FileSystemNode {
  private Map<String, FileSystemNode> children = new TreeMap<>();

  public Directory(String name) {
    super(name);
  }

  public void addChild(FileSystemNode node) {
    children.put(node.getName(), node);
  }

  public FileSystemNode getChild(String name) {
    return children.get(name);
  }

  @Override
  public FileSystemNode clone() {
    Directory newDir = new Directory(this.name);
    for (FileSystemNode child : this.children.values()) {
      newDir.addChild(child.clone());
    }
    return newDir;
  }

  @Override
  public void print(String indent) {
    System.out.println(indent + "+ " + name + " (Directory)");
    for (FileSystemNode child : children.values()) {
      child.print(indent + "  ");
    }
  }
}
