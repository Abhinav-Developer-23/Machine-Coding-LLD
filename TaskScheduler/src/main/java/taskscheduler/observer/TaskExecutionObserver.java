package taskscheduler.observer;

import taskscheduler.model.ScheduledTask;

public interface TaskExecutionObserver {
  void onTaskStarted(ScheduledTask task);

  void onTaskCompleted(ScheduledTask task);

  void onTaskFailed(ScheduledTask task, Exception e);

  void onTaskRetried(ScheduledTask task, int attempt, int maxRetries);
}
