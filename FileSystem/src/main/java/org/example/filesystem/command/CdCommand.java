package org.example.filesystem.command;

import org.example.filesystem.FileSystem;

public class CdCommand implements Command {
  private final FileSystem fs;
  private final String path;

  public CdCommand(FileSystem fs, String path) {
    this.fs = fs;
    this.path = path;
  }

  @Override
  public void execute() {
    fs.changeDirectory(path);
  }
}
