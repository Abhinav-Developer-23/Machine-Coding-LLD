package org.example.filesystem.strategy;

import org.example.filesystem.composite.Directory;
import org.example.filesystem.composite.FileSystemNode;

public class DetailedListingStrategy implements ListingStrategy {
  @Override
  public void list(Directory directory) {
    for (FileSystemNode node : directory.getChildren().values()) {
      char type = (node instanceof Directory) ? 'd' : 'f';
      System.out.println(type + "\t" + node.getName() + "\t" + node.getCreatedTime());
    }
  }
}
