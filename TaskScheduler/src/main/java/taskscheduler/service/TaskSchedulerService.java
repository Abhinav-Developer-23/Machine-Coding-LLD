package taskscheduler.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import taskscheduler.model.ScheduledTask;
import taskscheduler.observer.TaskExecutionObserver;
import taskscheduler.strategy.SchedulingStrategy;
import taskscheduler.task.Task;

public class TaskSchedulerService {
  private static final TaskSchedulerService INSTANCE = new TaskSchedulerService();

  private final PriorityBlockingQueue<ScheduledTask> taskQueue = new PriorityBlockingQueue<>();

  @Getter private final List<TaskExecutionObserver> observers = new ArrayList<>();

  private ThreadPoolExecutor executorService;
  private volatile boolean running = true;

  private TaskSchedulerService() {}

  public static TaskSchedulerService getInstance() {
    return INSTANCE;
  }

  public void initialize(int workerCount) {
    if (workerCount <= 0) {
      throw new IllegalArgumentException("Worker count must be >= 1");
    }
    ThreadFactory threadFactory =
        new ThreadFactory() {
          private final AtomicInteger counter = new AtomicInteger(0);

          @Override
          public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "WorkerThread-" + counter.getAndIncrement());
            t.setDaemon(true);
            return t;
          }
        };
    executorService =
        new ThreadPoolExecutor(
            workerCount,
            workerCount,
            0L,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(100),
            threadFactory);
    startWorkers(workerCount);
  }

  public String schedule(Task task, SchedulingStrategy strategy) {
    return schedule(task, strategy, 0);
  }

  public String schedule(Task task, SchedulingStrategy strategy, int maxRetries) {
    ScheduledTask scheduledTask = new ScheduledTask(task, strategy, maxRetries);
    taskQueue.put(scheduledTask);
    return scheduledTask.getId();
  }

  private void startWorkers(int workerCount) {
    for (int i = 0; i < workerCount; i++) {
      executorService.submit(this::runWorker);
    }
  }

  private void runWorker() {
    while (running) {
      try {
        // take() blocks until an element is available.
        ScheduledTask task = taskQueue.take();
        LocalDateTime now = LocalDateTime.now();
        long waitTime = 0;

        if (task.getNextExecutionTime().isAfter(now)) {
          waitTime = Duration.between(now, task.getNextExecutionTime()).toMillis();
        }

        if (waitTime > 0) {
          // Wait for the scheduled time.
          Thread.sleep(waitTime);
        }

        // Check if a higher-priority task has arrived while we were sleeping
        ScheduledTask head = taskQueue.peek();
        if (head != null && head.compareTo(task) < 0) {
          taskQueue.put(task); // Put our task back and let the higher-priority one run
          continue;
        }

        // --- Execute the task ---
        execute(task);
      } catch (InterruptedException e) {
        // This is the expected way to stop the worker thread.
        Thread.currentThread().interrupt();
        break; // Exit the loop
      }
    }
    System.out.printf("%s stopped.%n", Thread.currentThread().getName());
  }

  private void execute(ScheduledTask task) {
    observers.forEach(o -> o.onTaskStarted(task));
    boolean succeeded = false;
    boolean retrying = false;

    try {
      task.getTask().execute();
      succeeded = true;
    } catch (Exception e) {
      observers.forEach(o -> o.onTaskFailed(task, e));

      if (task.canRetry()) {
        task.incrementRetryCount();
        observers.forEach(
            o -> o.onTaskRetried(task, task.getRetryCount(), task.getMaxRetries()));
        taskQueue.put(task); // Re-queue immediately for retry
        retrying = true;
      } else {
        System.err.printf(
            "Task %s exhausted all %d retries. Giving up.%n",
            task.getId(), task.getMaxRetries());
      }
    }

    if (retrying) {
      return; // Don't reschedule — already re-queued for retry
    }

    if (succeeded) {
      task.updateLastExecutionTime();
      task.resetRetryCount();
      observers.forEach(o -> o.onTaskCompleted(task));
    } else {
      // Failed and no retries left — mark as executed so the strategy can advance/terminate
      task.updateLastExecutionTime();
    }

    // --- Normal re-scheduling logic ---
    task.updateNextExecutionTime();

    if (task.hasMoreExecutions()) {
      taskQueue.put(task); // Re-queue for the next scheduled run.
    } else {
      System.out.printf(
          "Task %s has no more executions and will not be rescheduled.%n", task.getId());
    }
  }

  public void shutdown() {
    running = false;
    executorService.shutdownNow(); // interrupts all workers blocked on take()
    try {
      if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
        System.err.println("Warning: Some workers did not terminate in time.");
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    System.out.println("Scheduler shut down.");
  }

  public void addObserver(TaskExecutionObserver observer) {
    observers.add(observer);
  }
}
