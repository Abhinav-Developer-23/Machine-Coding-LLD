package org.example.filesystem.command;

import org.example.filesystem.FileSystem;
import org.example.filesystem.strategy.ListingStrategy;

public class LsCommand implements Command {
  private final FileSystem fs;
  private final String path; // Path can be null, meaning "current directory"
  private final ListingStrategy strategy;

  public LsCommand(FileSystem fs, String path, ListingStrategy strategy) {
    this.fs = fs;
    this.path = path;
    this.strategy = strategy;
  }

  @Override
  public void execute() {
    if (path == null) {
      fs.listContents(strategy);
    } else {
      fs.listContents(path, strategy);
    }
  }
}
