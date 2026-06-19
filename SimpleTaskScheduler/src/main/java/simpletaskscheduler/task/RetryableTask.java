package simpletaskscheduler.task;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import simpletaskscheduler.observer.TaskObserver;

/**
 * Wraps any Runnable with retry logic. When submitted to a ThreadPoolExecutor, it runs the
 * underlying Runnable and retries up to maxRetries times on failure.
 */
@Getter
public class RetryableTask implements Runnable {
  private final String name;
  private final Runnable task;
  private final int maxRetries;
  private final List<TaskObserver> observers;

  public RetryableTask(String name, Runnable task, int maxRetries, List<TaskObserver> observers) {
    this.name = name;
    this.task = task;
    this.maxRetries = maxRetries;
    this.observers = observers;
  }

  @Override
  public void run() {
    int totalAttempts = maxRetries + 1; // 1 initial + maxRetries

    for (int attempt = 1; attempt <= totalAttempts; attempt++) {
      observers.forEach(o -> o.onTaskStarted(name));

      try {
        task.run();
        observers.forEach(o -> o.onTaskCompleted(name));
        return; // Success — exit
      } catch (Exception e) {
        int currentAttempt = attempt;
        observers.forEach(o -> o.onTaskFailed(name, currentAttempt, totalAttempts, e));

        if (attempt < totalAttempts) {
          observers.forEach(o -> o.onTaskRetrying(name, currentAttempt, totalAttempts));
        } else {
          observers.forEach(o -> o.onTaskExhausted(name, totalAttempts));
        }
      }
    }
  }
}
