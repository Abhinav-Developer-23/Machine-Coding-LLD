package simpletaskscheduler.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import simpletaskscheduler.observer.TaskObserver;
import simpletaskscheduler.task.RetryableTask;

/**
 * Simple task scheduler backed by a ThreadPoolExecutor. Submit any Runnable with a retry count —
 * it wraps it in a RetryableTask and submits to the pool.
 */
public class TaskScheduler {
  private static final TaskScheduler INSTANCE = new TaskScheduler();

  private ThreadPoolExecutor executor;
  @Getter private final List<TaskObserver> observers = new ArrayList<>();

  private TaskScheduler() {}

  public static TaskScheduler getInstance() {
    return INSTANCE;
  }

  public void initialize(int threadCount) {
    if (threadCount <= 0) {
      throw new IllegalArgumentException("Thread count must be >= 1");
    }

    executor =
        new ThreadPoolExecutor(
            threadCount, threadCount, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());
  }

  /**
   * Submit a Runnable with no retries.
   */
  public void submit(String taskName, Runnable task) {
    submit(taskName, task, 0);
  }

  /**
   * Submit a Runnable with the given number of retries on failure.
   */
  public void submit(String taskName, Runnable task, int maxRetries) {
    RetryableTask retryable = new RetryableTask(taskName, task, maxRetries, observers);
    executor.submit(retryable);
  }

  public void addObserver(TaskObserver observer) {
    observers.add(observer);
  }

  public void shutdown() {
    executor.shutdown();
    try {
      if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
        executor.shutdownNow();
        System.err.println("Warning: Some tasks did not finish in time.");
      }
    } catch (InterruptedException e) {
      executor.shutdownNow();
      Thread.currentThread().interrupt();
    }
    System.out.println("Scheduler shut down.");
  }
}
