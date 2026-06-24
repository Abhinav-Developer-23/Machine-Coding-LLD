package versioncontrol;

import java.util.HashMap;
import java.util.Map;
import versioncontrol.entities.Commit;
import versioncontrol.entities.Directory;

public class CommitManager {
  private final Map<String, Commit> commits = new HashMap<>();

  public Commit createCommit(String author, String message, Commit parent, Directory rootSnapshot) {
    Commit newCommit = new Commit(author, message, parent, rootSnapshot);
    commits.put(newCommit.getId(), newCommit);
    return newCommit;
  }

  public Commit getCommit(String commitId) {
    return commits.get(commitId);
  }

  public void printHistory(Commit headCommit) {
    if (headCommit == null) {
      System.out.println("No commits in history.");
      return;
    }

    Commit current = headCommit;
    while (current != null) {
      System.out.println("Commit: " + current.getId());
      System.out.println("Author: " + current.getAuthor());
      System.out.println("Date: " + current.getTimestamp());
      System.out.println("Message: " + current.getMessage());
      System.out.println("--------------------");
      current = current.getParent();
    }
  }
}
