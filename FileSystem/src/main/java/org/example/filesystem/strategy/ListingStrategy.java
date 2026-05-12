package org.example.filesystem.strategy;

import org.example.filesystem.composite.Directory;

public interface ListingStrategy {
  void list(Directory directory);
}
