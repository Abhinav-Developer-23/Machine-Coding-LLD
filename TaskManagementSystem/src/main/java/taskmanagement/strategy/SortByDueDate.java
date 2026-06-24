package taskmanagement.strategy;

import java.util.Comparator;
import java.util.List;
import taskmanagement.entities.Task;

public class SortByDueDate implements TaskSortStrategy {
  @Override
  public void sort(List<Task> tasks) {
    tasks.sort(Comparator.comparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder())));
  }
}
