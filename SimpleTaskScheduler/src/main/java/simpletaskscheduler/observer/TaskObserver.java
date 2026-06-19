package simpletaskscheduler.observer;

/**
 * Observer for task lifecycle events.
 */
public interface TaskObserver {
  void onTaskStarted(String taskName);

  void onTaskCompleted(String taskName);

  void onTaskFailed(String taskName, int attempt, int maxAttempts, Exception e);

  void onTaskRetrying(String taskName, int attempt, int maxAttempts);

  void onTaskExhausted(String taskName, int maxAttempts);
}
