package taskmanagement;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import taskmanagement.entities.Task;
import taskmanagement.entities.TaskList;
import taskmanagement.entities.User;
import taskmanagement.enums.TaskPriority;
import taskmanagement.enums.TaskStatus;
import taskmanagement.strategy.TaskSortStrategy;

public class TaskManagementSystem {
  private final Map<String, User> users;
  private final Map<String, Task> tasks;
  private final Map<String, TaskList> taskLists;

  public TaskManagementSystem() {
    users = new ConcurrentHashMap<>();
    tasks = new ConcurrentHashMap<>();
    taskLists = new ConcurrentHashMap<>();
  }

  public User createUser(String name, String email) {
    User user = new User(name, email);
    users.put(user.getId(), user);
    return user;
  }

  public TaskList createTaskList(String listName) {
    TaskList taskList = new TaskList(listName);
    taskLists.put(taskList.getId(), taskList);
    return taskList;
  }

  public Task createTask(
      String title,
      String description,
      LocalDate dueDate,
      TaskPriority priority,
      String createdByUserId) {
    User createdBy = users.get(createdByUserId);
    if (createdBy == null) {
      throw new IllegalArgumentException("User not found: " + createdByUserId);
    }

    Task task =
        new Task.TaskBuilder(title)
            .description(description)
            .dueDate(dueDate)
            .priority(priority)
            .createdBy(createdBy)
            .build();

    tasks.put(task.getId(), task);
    return task;
  }

  public List<Task> listTasksByUser(String userId) {
    return tasks.values().stream()
        .filter(task -> task.getAssignee() != null && task.getAssignee().getId().equals(userId))
        .collect(Collectors.toList());
  }

  public List<Task> listTasksByStatus(TaskStatus status) {
    return tasks.values().stream()
        .filter(task -> task.getStatus() == status)
        .sorted(Comparator.comparing(Task::getTitle))
        .collect(Collectors.toList());
  }

  public void deleteTask(String taskId) {
    tasks.remove(taskId);
  }

  public List<Task> searchTasks(String keyword, TaskSortStrategy sortingStrategy) {
    String lowerKeyword = keyword.toLowerCase();
    List<Task> result =
        tasks.values().stream()
            .filter(
                task ->
                    task.getTitle().toLowerCase().contains(lowerKeyword)
                        || task.getDescription().toLowerCase().contains(lowerKeyword))
            .collect(Collectors.toCollection(ArrayList::new));

    sortingStrategy.sort(result);
    return result;
  }
}
