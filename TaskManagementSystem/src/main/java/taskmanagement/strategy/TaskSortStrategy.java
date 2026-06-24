package taskmanagement.strategy;

import java.util.List;
import taskmanagement.entities.Task;

public interface TaskSortStrategy {
  void sort(List<Task> tasks);
}
