package versioncontrol;

import versioncontrol.entities.Commit;
import versioncontrol.entities.Directory;

public class VersionControlSystem {
  private static VersionControlSystem instance;
  private final CommitManager commitManager;
  private final BranchManager branchManager;
  private Directory workingDirectory;

  private VersionControlSystem() {
    this.commitManager = new CommitManager();
    this.workingDirectory = new Directory("root");
    Commit initialCommit =
        commitManager.createCommit(
            "system", "Initial commit", null, (Directory) workingDirectory.clone());
    this.branchManager = new BranchManager(initialCommit);
  }

  public static synchronized VersionControlSystem getInstance() {
    if (instance == null) {
      instance = new VersionControlSystem();
    }
    return instance;
  }

  public Directory getWorkingDirectory() {
    return workingDirectory;
  }

  public String commit(String author, String message) {
    Commit parentCommit = branchManager.getCurrentBranch().getHead();
    Directory snapshot = (Directory) workingDirectory.clone();

    Commit newCommit = commitManager.createCommit(author, message, parentCommit, snapshot);
    branchManager.updateHead(newCommit);

    System.out.println(
        "Committed "
            + newCommit.getId()
            + " to branch "
            + branchManager.getCurrentBranch().getName());
    return newCommit.getId();
  }

  public void createBranch(String name) {
    Commit head = branchManager.getCurrentBranch().getHead();
    branchManager.createBranch(name, head);
  }

  public void checkoutBranch(String name) {
    boolean success = branchManager.switchBranch(name);
    if (success) {
      Commit newHead = branchManager.getCurrentBranch().getHead();
      this.workingDirectory = (Directory) newHead.getRootSnapshot().clone();
    }
  }

  public void revert(String commitId) {
    Commit targetCommit = commitManager.getCommit(commitId);
    if (targetCommit == null) {
      System.out.println("Error: Commit '" + commitId + "' not found.");
      return;
    }
    this.workingDirectory = (Directory) targetCommit.getRootSnapshot().clone();
    branchManager.updateHead(targetCommit);

    System.out.println("Repository state reverted to commit " + commitId);
  }

  public void log() {
    System.out.println(
        "\n--- Commit History for branch '" + branchManager.getCurrentBranch().getName() + "' ---");
    Commit headCommit = branchManager.getCurrentBranch().getHead();
    commitManager.printHistory(headCommit);
  }

  public void printCurrentState() {
    System.out.println("\n--- Current Working Directory State ---");
    workingDirectory.print("");
  }
}
