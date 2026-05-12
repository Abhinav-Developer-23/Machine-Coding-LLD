package org.example.filesystem.command;

import org.example.filesystem.FileSystem;

public class PwdCommand implements Command {
  private final FileSystem fs;

  public PwdCommand(FileSystem fs) {
    this.fs = fs;
  }

  @Override
  public void execute() {
    System.out.println(fs.getWorkingDirectory());
  }
}
