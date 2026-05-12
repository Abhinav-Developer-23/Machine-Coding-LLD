package org.example.filesystem;

import java.util.Arrays;
import org.example.filesystem.command.*;
import org.example.filesystem.strategy.*;

public class Shell {
  private final FileSystem fs;

  public Shell() {
    this.fs = FileSystem.getInstance();
  }

  public void executeCommand(String input) {
    String[] parts = input.trim().split("\\s+");
    String commandName = parts[0];

    Command command;

    try {
      switch (commandName) {
        case "mkdir":
          command = new MkdirCommand(fs, parts[1]);
          break;
        case "touch":
          command = new TouchCommand(fs, parts[1]);
          break;
        case "cd":
          command = new CdCommand(fs, parts[1]);
          break;
        case "ls":
          command = new LsCommand(fs, getPathArgumentForLs(parts), getListingStrategy(parts));
          break;
        case "pwd":
          command = new PwdCommand(fs);
          break;
        case "cat":
          command = new CatCommand(fs, parts[1]);
          break;
        case "echo":
          command = new EchoCommand(fs, getEchoContent(input), getEchoFilePath(parts));
          break;
        default:
          command = () -> System.err.println("Error: Unknown command '" + commandName + "'.");
          break;
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      System.out.println("Error: Missing argument for command '" + commandName + "'.");
      command = () -> {}; // No-op command
    }

    command.execute();
  }

  private ListingStrategy getListingStrategy(String[] args) {
    if (Arrays.asList(args).contains("-l")) {
      return new DetailedListingStrategy();
    }
    return new SimpleListingStrategy();
  }

  private String getPathArgumentForLs(String[] parts) {
    // Find the first argument that is not an option flag.
    return Arrays.stream(parts)
        .skip(1) // Skip the command name itself
        .filter(part -> !part.startsWith("-"))
        .findFirst()
        .orElse(null); // Return null if no path argument is found
  }

  private String getEchoContent(String input) {
    // Simple parsing for "echo 'content' > file"
    try {
      return input.substring(input.indexOf("'") + 1, input.lastIndexOf("'"));
    } catch (Exception e) {
      return "";
    }
  }

  private String getEchoFilePath(String[] parts) {
    // The file path is the last argument after the redirection symbol '>'
    for (int i = 0; i < parts.length; i++) {
      if (">".equals(parts[i]) && i + 1 < parts.length) {
        return parts[i + 1];
      }
    }
    return ""; // Should be handled by argument check
  }
}
