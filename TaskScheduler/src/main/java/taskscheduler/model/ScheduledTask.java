package taskscheduler.model;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import taskscheduler.strategy.SchedulingStrategy;
import taskscheduler.task.Task;

@Getter
public class ScheduledTask implements Comparable<ScheduledTask> {
  private final String id;
  private final Task task;
  private final SchedulingStrategy strategy;
  private final int maxRetries;
  private int retryCount;
  private LocalDateTime nextExecutionTime;
  private LocalDateTime lastExecutionTime;

  public ScheduledTask(Task task, SchedulingStrategy strategy) {
    this(task, strategy, 0);
  }

  public ScheduledTask(Task task, SchedulingStrategy strategy, int maxRetries) {
    this.id = UUID.randomUUID().toString();
    this.task = task;
    this.strategy = strategy;
    this.maxRetries = maxRetries;
    this.retryCount = 0;
    updateNextExecutionTime();
  }

  public void updateNextExecutionTime() {
    Optional<LocalDateTime> nextTime = strategy.getNextExecutionTime(this.lastExecutionTime);
    this.nextExecutionTime = nextTime.orElse(null);
  }

  public void updateLastExecutionTime() {
    this.lastExecutionTime = nextExecutionTime;
  }

  @Override
  public int compareTo(ScheduledTask other) {
    return this.nextExecutionTime.compareTo(other.nextExecutionTime);
  }

  public boolean hasMoreExecutions() {
    return nextExecutionTime != null;
  }

  public boolean canRetry() {
    return retryCount < maxRetries;
  }

  public void incrementRetryCount() {
    retryCount++;
  }

  public void resetRetryCount() {
    retryCount = 0;
  }
}
