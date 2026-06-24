package versioncontrol;

import java.util.HashMap;
import java.util.Map;
import versioncontrol.entities.Branch;
import versioncontrol.entities.Commit;

public class BranchManager {
  private final Map<String, Branch> branches = new HashMap<>();
  private Branch currentBranch;

  public BranchManager(Commit initialCommit) {
    Branch mainBranch = new Branch("main", initialCommit);
    this.branches.put("main", mainBranch);
    this.currentBranch = mainBranch;
  }

  public void createBranch(String name, Commit head) {
    if (branches.containsKey(name)) {
      System.out.println("Error: Branch '" + name + "' already exists.");
      return;
    }
    Branch newBranch = new Branch(name, head);
    branches.put(name, newBranch);
    System.out.println("Created branch '" + name + "'.");
  }

  public boolean switchBranch(String name) {
    if (!branches.containsKey(name)) {
      System.out.println("Error: Branch '" + name + "' not found.");
      return false;
    }
    this.currentBranch = branches.get(name);
    System.out.println("Switched to branch '" + name + "'.");
    return true;
  }

  public void updateHead(Commit newHead) {
    this.currentBranch.setHead(newHead);
  }

  public Branch getCurrentBranch() {
    return currentBranch;
  }
}
