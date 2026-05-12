package org.example.filesystem.command;

import org.example.filesystem.FileSystem;

public class TouchCommand implements Command {
  private final FileSystem fs;
  private final String path;

  public TouchCommand(FileSystem fs, String path) {
    this.fs = fs;
    this.path = path;
  }

  @Override
  public void execute() {
    fs.createFile(path);
  }
}
