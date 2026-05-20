package org.example.filesystem;

import java.util.Arrays;
import org.example.filesystem.command.*;
import org.example.filesystem.strategy.*;

/**
 * A <em>shell</em> is the layer between a person (or test harness) typing text commands and the
 * underlying system that actually performs work. It reads a line of input, figures out which
 * operation was requested, and runs it—much like {@code bash} or {@code cmd.exe} sit between you
 * and the operating system.
 *
 * <p>This class is necessary because raw {@link FileSystem} APIs describe <em>what</em> the
 * filesystem can do (mkdir, read, write, etc.), but not the <em>text protocol</em> users type. The
 * shell owns parsing and dispatch: it splits the input line, chooses the right {@link Command}, and
 * executes it against the shared {@link FileSystem} instance. Without it, every caller would
 * duplicate command names, argument rules, and error handling.
 */
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
