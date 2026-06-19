package simpletaskscheduler;

import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;
import simpletaskscheduler.observer.LoggingObserver;
import simpletaskscheduler.scheduler.TaskScheduler;

public class SimpleTaskSchedulerDemo {
  public static void main(String[] args) throws InterruptedException {
    TaskScheduler scheduler = TaskScheduler.getInstance();
    scheduler.addObserver(new LoggingObserver());
    scheduler.initialize(4);

    // 1. Simple task — no retries
    Runnable printTask = () ->
        System.out.printf("[%s] Hello from a simple Runnable!%n", LocalTime.now());
    scheduler.submit("PrintTask", printTask);

    // 2. Task that always succeeds — with retries configured (retries won't be used)
    Runnable successTask = () ->
        System.out.printf("[%s] Doing important work... done!%n", LocalTime.now());
    scheduler.submit("SuccessTask", successTask, 3);

    // 3. Task that fails 2 times then succeeds — 3 retries allowed
    AtomicInteger counter1 = new AtomicInteger(0);
    Runnable recoverableTask = () -> {
      int attempt = counter1.incrementAndGet();
      if (attempt <= 2) {
        throw new RuntimeException("Boom on attempt " + attempt);
      }
      System.out.printf("[%s] RecoverableTask finally succeeded on attempt %d!%n",
          LocalTime.now(), attempt);
    };
    scheduler.submit("RecoverableTask", recoverableTask, 3);

    // 4. Task that always fails — 2 retries allowed
    Runnable hopelessTask = () -> {
      throw new RuntimeException("I always fail!");
    };
    scheduler.submit("HopelessTask", hopelessTask, 2);

    // Wait for all tasks to finish
    Thread.sleep(2000);
    scheduler.shutdown();
  }
}
