package org.example.filesystem.strategy;

import org.example.filesystem.composite.Directory;

public class SimpleListingStrategy implements ListingStrategy {
  @Override
  public void list(Directory directory) {
    directory.getChildren().keySet().forEach(name -> System.out.print(name + "  "));
    System.out.println();
  }
}
