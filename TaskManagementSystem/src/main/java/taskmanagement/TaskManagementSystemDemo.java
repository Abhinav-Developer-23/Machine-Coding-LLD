package taskmanagement;

import java.time.LocalDate;
import java.util.List;
import taskmanagement.entities.Task;
import taskmanagement.entities.TaskList;
import taskmanagement.entities.User;
import taskmanagement.enums.TaskPriority;
import taskmanagement.enums.TaskStatus;
import taskmanagement.strategy.SortByDueDate;

public class TaskManagementSystemDemo {
  public static void main(String[] args) {
    try {
      TaskManagementSystem taskManagementSystem = new TaskManagementSystem();

      User user1 = taskManagementSystem.createUser("John Doe", "john@example.com");
      User user2 = taskManagementSystem.createUser("Jane Smith", "jane@example.com");

      TaskList taskList1 = taskManagementSystem.createTaskList("Enhancements");
      TaskList taskList2 = taskManagementSystem.createTaskList("Bug Fix");

      Task task1 =
          taskManagementSystem.createTask(
              "Enhancement Task",
              "Launch New Feature",
              LocalDate.now().plusDays(2),
              TaskPriority.LOW,
              user1.getId());
      Task subtask1 =
          taskManagementSystem.createTask(
              "Enhancement sub task",
              "Design UI/UX",
              LocalDate.now().plusDays(1),
              TaskPriority.MEDIUM,
              user1.getId());
      Task task2 =
          taskManagementSystem.createTask(
              "Bug Fix Task",
              "Fix API Bug",
              LocalDate.now().plusDays(3),
              TaskPriority.HIGH,
              user2.getId());

      task1.addSubtask(subtask1);

      taskList1.addTask(task1);
      taskList2.addTask(task2);

      taskList1.display();

      subtask1.startProgress();
      subtask1.setAssignee(user2);

      taskList1.display();

      List<Task> searchResults = taskManagementSystem.searchTasks("Task", new SortByDueDate());
      System.out.println("\nTasks with keyword Task:");
      for (Task task : searchResults) {
        System.out.println(task.getTitle());
      }

      List<Task> filteredTasks = taskManagementSystem.listTasksByStatus(TaskStatus.TODO);
      System.out.println("\nTODO Tasks:");
      for (Task task : filteredTasks) {
        System.out.println(task.getTitle());
      }

      subtask1.completeTask();

      List<Task> userTaskList = taskManagementSystem.listTasksByUser(user2.getId());
      System.out.println("\nTask for " + user2.getName() + ":");
      for (Task task : userTaskList) {
        System.out.println(task.getTitle());
      }

      taskList1.display();

      taskManagementSystem.deleteTask(task2.getId());
    } catch (Exception e) {
      System.out.println("Demo stopped early: " + e.getMessage());
    }
  }
}
