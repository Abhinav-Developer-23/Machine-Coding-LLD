package org.example.filesystem;

import org.example.filesystem.composite.Directory;
import org.example.filesystem.composite.File;
import org.example.filesystem.composite.FileSystemNode;
import org.example.filesystem.strategy.ListingStrategy;

public class FileSystem {
  private static volatile FileSystem instance;
  private final Directory root;
  private Directory currentDirectory;

  /**
   * Private constructor to enforce Singleton pattern. Step 1: Initialize the root directory to "/".
   * Step 2: Set the current directory to point to the root.
   */
  private FileSystem() {
    this.root = new Directory("/", null);
    this.currentDirectory = root;
  }

  /**
   * Returns the singleton instance of the FileSystem. Step 1: Checks if the singleton instance is
   * already created. Step 2: If it isn't, synchronizes the class to make it thread-safe. Step 3:
   * Double-checks if the instance is still null and creates it if so. Step 4: Returns the single
   * instance.
   *
   * @return the singleton instance of FileSystem
   */
  public static FileSystem getInstance() {
    if (instance == null) {
      synchronized (FileSystem.class) {
        if (instance == null) {
          instance = new FileSystem();
        }
      }
    }
    return instance;
  }

  /**
   * Creates a new directory at the specified path. Step 1: Calls the internal helper method
   * createNode() and passes the path. Step 2: Passes true for the isDirectory flag to specify that
   * a directory should be created.
   *
   * @param path the path where the directory should be created
   */
  public void createDirectory(String path) {
    createNode(path, true);
  }

  /**
   * Creates a new file at the specified path. Step 1: Calls the internal helper method createNode()
   * and passes the path. Step 2: Passes false for the isDirectory flag to specify that a file
   * should be created.
   *
   * @param path the path where the file should be created
   */
  public void createFile(String path) {
    createNode(path, false);
  }

  /**
   * Changes the current working directory to the specified path. Step 1: Calls getNode() to
   * retrieve the FileSystemNode at the given path. Step 2: Checks if the retrieved node is actually
   * a Directory. Step 3: If it is a directory, it updates currentDirectory to point to this new
   * directory. Otherwise, it prints an error.
   *
   * @param path the path of the target directory
   */
  public void changeDirectory(String path) {
    FileSystemNode node = getNode(path);
    if (node instanceof Directory) {
      currentDirectory = (Directory) node;
    } else {
      System.out.println("Error: '" + path + "' is not a directory.");
    }
  }

  /**
   * Lists the contents of the current working directory using the provided strategy. Step 1: Calls
   * the list() method on the provided strategy object. Step 2: Passes the currentDirectory so that
   * the strategy lists the contents of where we currently are.
   *
   * @param strategy the listing strategy to use (e.g., simple or detailed)
   */
  public void listContents(ListingStrategy strategy) {
    strategy.list(currentDirectory);
  }

  /**
   * Lists the contents of the specified path using the provided strategy. Step 1: Resolves the
   * provided path to a node using getNode(). Step 2: If the path is invalid, it prints an error and
   * stops. Step 3: If the resolved node is a directory, it executes the listing strategy on that
   * directory. Step 4: If the resolved node is a file, it mimics standard Unix behavior by just
   * printing the file's name.
   *
   * @param path the path to list contents of
   * @param strategy the listing strategy to use
   */
  public void listContents(String path, ListingStrategy strategy) {
    FileSystemNode node = getNode(path);
    if (node == null) {
      System.err.println("ls: cannot access '" + path + "': No such file or directory");
      return;
    }

    if (node instanceof Directory) {
      strategy.list((Directory) node);
    } else {
      // Mimic Unix behavior: if ls is pointed at a file, it just prints the file name.
      System.out.println(node.getName());
    }
  }

  /**
   * Retrieves the absolute path of the current working directory. Step 1: Returns the full path
   * string of the currentDirectory by calling getPath() on it.
   *
   * @return the path of the current directory
   */
  public String getWorkingDirectory() {
    return currentDirectory.getPath();
  }

  /**
   * Writes the specified content to the file at the given path. Step 1: Resolves the path to a node
   * using getNode(). Step 2: Checks if the returned node is an instance of a File. Step 3: If it
   * is, it casts the node to a File and calls setContent() to overwrite its content. Otherwise, it
   * prints an error.
   *
   * @param path the path of the file to write to
   * @param content the content to write into the file
   */
  public void writeToFile(String path, String content) {
    FileSystemNode node = getNode(path);
    if (node instanceof File) {
      ((File) node).setContent(content);
    } else {
      System.out.println(
          "Error: Cannot write to '" + path + "'. It is not a file or does not exist.");
    }
  }

  /**
   * Reads and returns the content of the file at the specified path. Step 1: Resolves the path to a
   * node using getNode(). Step 2: Checks if the node is a File. Step 3: If it is, it returns the
   * content of the file. If not, it prints an error and returns an empty string.
   *
   * @param path the path of the file to read
   * @return the content of the file, or an empty string if the file cannot be read
   */
  public String readFile(String path) {
    FileSystemNode node = getNode(path);
    if (node instanceof File) {
      return ((File) node).getContent();
    }
    System.out.println(
        "Error: Cannot read from '" + path + "'. It is not a file or does not exist.");
    return "";
  }

  // --- Private Helper Methods ---

  /**
   * Helper method to create a file or directory node at the specified path. Step 1: Splits the path
   * to figure out the parent directory path and the name of the new node to be created. Step 2: If
   * the path includes slashes (nested), it finds the parent directory node using getNode(). If the
   * path is simple, it uses the currentDirectory. Step 3: Validates that the target parent is
   * actually a directory and ensures the node name isn't empty or already taken. Step 4: Creates
   * either a Directory or File object based on the isDirectory flag and adds it as a child to the
   * parent. Remember we do not create where path does not already exist we create if path does
   * exists
   */
  private void createNode(String path, boolean isDirectory) {
    String name;
    Directory parent;

    if (path.contains("/")) {
      // Path has directory components (e.g., "/a/b/c" or "b/c")
      int lastSlashIndex = path.lastIndexOf('/');
      name = path.substring(lastSlashIndex + 1);
      String parentPath = path.substring(0, lastSlashIndex);

      // Handle creating in root, e.g., "/testfile"
      if (parentPath.isEmpty()) {
        parentPath = "/";
      }

      FileSystemNode parentNode = getNode(parentPath);
      if (!(parentNode instanceof Directory)) {
        System.out.println(
            "Error: Invalid path. Parent '"
                + parentPath
                + "' is not a directory or does not exist.");
        return;
      }
      parent = (Directory) parentNode;
    } else {
      // Path is a simple name in the current directory (e.g., "c")
      name = path;
      parent = currentDirectory;
    }

    if (name.isEmpty()) {
      System.err.println("Error: File or directory name cannot be empty.");
      return;
    }

    // --- Common logic from here ---
    if (parent.getChild(name) != null) {
      System.out.println(
          "Error: Node '" + name + "' already exists in '" + parent.getPath() + "'.");
      return;
    }

    FileSystemNode newNode = isDirectory ? new Directory(name, parent) : new File(name, parent);
    parent.addChild(newNode);
  }

  /**
   * Helper method to resolve a path string to a FileSystemNode. Step 1: Checks if the path is
   * exactly "/" (root) and returns it immediately. Step 2: Determines the starting node: root if
   * the path is absolute (starts with "/"), or currentDirectory if it's relative. Step 3: Splits
   * the path by slashes and loops through each component. Step 4: Handles navigation logic: if the
   * part is "..", it moves up to the parent. Otherwise, it looks for a child matching the part
   * name. Returns null if any part of the path is invalid.
   *
   * @param path the path to resolve
   * @return the corresponding FileSystemNode, or null if the path is invalid
   */
  private FileSystemNode getNode(String path) {
    if (path.equals("/")) return root;

    Directory startDir = path.startsWith("/") ? root : currentDirectory;
    // Use a non-empty string split to handle leading/trailing slashes gracefully
    String[] parts = path.split("/");

    FileSystemNode current = startDir;
    for (String part : parts) {
      if (part.isEmpty() || part.equals(".")) {
        continue;
      }
      if (!(current instanceof Directory)) {
        return null; // Part of the path is a file, so it's invalid
      }

      if (part.equals("..")) {
        current = current.getParent();
        if (current == null) current = root; // Can't go above root
      } else {
        current = ((Directory) current).getChild(part);
      }

      if (current == null) return null; // Path component does not exist
    }
    return current;
  }
}
