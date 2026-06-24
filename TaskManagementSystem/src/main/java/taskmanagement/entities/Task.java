package taskmanagement.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import taskmanagement.enums.TaskPriority;
import taskmanagement.enums.TaskStatus;
import taskmanagement.states.TaskState;
import taskmanagement.states.TodoState;

@Getter
public class Task {
  private final String id;
  private String title;
  private String description;
  private LocalDate dueDate;
  private TaskPriority priority;
  private final User createdBy;
  private User assignee;
  private TaskState currentState;
  private final Set<Tag> tags;
  private final List<Comment> comments;
  private final List<Task> subtasks;
  private final List<ActivityLog> activityLogs;

  private Task(TaskBuilder builder) {
    this.id = builder.id;
    this.title = builder.title;
    this.description = builder.description;
    this.dueDate = builder.dueDate;
    this.priority = builder.priority;
    this.createdBy = builder.createdBy;
    this.assignee = builder.assignee;
    this.tags = builder.tags != null ? builder.tags : new HashSet<>();
    this.currentState = new TodoState();
    this.comments = new ArrayList<>();
    this.subtasks = new ArrayList<>();
    this.activityLogs = new ArrayList<>();
    addLog("Task created with title: " + title);
  }

  public synchronized void setAssignee(User user) {
    this.assignee = user;
    addLog("Assigned to " + user.getName());
  }

  public synchronized void updatePriority(TaskPriority priority) {
    this.priority = priority;
  }

  public synchronized void addComment(Comment comment) {
    comments.add(comment);
    addLog("Comment added by " + comment.getAuthor().getName());
  }

  public synchronized void addSubtask(Task subtask) {
    subtasks.add(subtask);
    addLog("Subtask added: " + subtask.getTitle());
  }

  public void setState(TaskState state) {
    this.currentState = state;
    addLog("Status changed to: " + state.getStatus());
  }

  public void startProgress() {
    currentState.startProgress(this);
  }

  public void completeTask() {
    currentState.completeTask(this);
  }

  public void reopenTask() {
    currentState.reopenTask(this);
  }

  public void blockTask() {
    currentState.blockTask(this);
  }

  public boolean canComplete() {
    for (Task subtask : subtasks) {
      if (subtask.getStatus() != TaskStatus.DONE) {
        return false;
      }
    }
    return true;
  }

  public void addLog(String logDescription) {
    this.activityLogs.add(new ActivityLog(logDescription));
  }

  public boolean isComposite() {
    return !subtasks.isEmpty();
  }

  public void display(String indent) {
    System.out.println(
        indent
            + "- "
            + title
            + " ["
            + getStatus()
            + ", "
            + priority
            + ", Due: "
            + dueDate
            + "]");
    if (isComposite()) {
      for (Task subtask : subtasks) {
        subtask.display(indent + "  ");
      }
    }
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public TaskStatus getStatus() {
    return currentState.getStatus();
  }

  public static class TaskBuilder {
    private final String id;
    private String title;
    private String description = "";
    private LocalDate dueDate;
    private TaskPriority priority;
    private User createdBy;
    private User assignee;
    private Set<Tag> tags;

    public TaskBuilder(String title) {
      this.id = UUID.randomUUID().toString();
      this.title = title;
    }

    public TaskBuilder description(String description) {
      this.description = description;
      return this;
    }

    public TaskBuilder dueDate(LocalDate dueDate) {
      this.dueDate = dueDate;
      return this;
    }

    public TaskBuilder priority(TaskPriority priority) {
      this.priority = priority;
      return this;
    }

    public TaskBuilder assignee(User assignee) {
      this.assignee = assignee;
      return this;
    }

    public TaskBuilder createdBy(User createdBy) {
      this.createdBy = createdBy;
      return this;
    }

    public TaskBuilder tags(Set<Tag> tags) {
      this.tags = tags;
      return this;
    }

    public Task build() {
      return new Task(this);
    }
  }
}
