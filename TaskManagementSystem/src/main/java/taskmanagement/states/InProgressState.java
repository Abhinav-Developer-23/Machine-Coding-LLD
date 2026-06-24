package taskmanagement.states;

import taskmanagement.entities.Task;
import taskmanagement.enums.TaskStatus;

public class InProgressState implements TaskState {
  @Override
  public void startProgress(Task task) {
    System.out.println("Task is already in progress.");
  }

  @Override
  public void completeTask(Task task) {
    if (task.canComplete()) {
      task.setState(new DoneState());
    } else {
      System.out.println("Cannot complete task until all subtasks are done.");
    }
  }

  @Override
  public void reopenTask(Task task) {
    task.setState(new TodoState());
  }

  @Override
  public void blockTask(Task task) {
    task.setState(new BlockedState());
  }

  @Override
  public TaskStatus getStatus() {
    return TaskStatus.IN_PROGRESS;
  }
}
