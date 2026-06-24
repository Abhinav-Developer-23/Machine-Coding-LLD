package taskmanagement.states;

import taskmanagement.entities.Task;
import taskmanagement.enums.TaskStatus;

public interface TaskState {
  void startProgress(Task task);

  void completeTask(Task task);

  void blockTask(Task task);

  void reopenTask(Task task);

  TaskStatus getStatus();
}
