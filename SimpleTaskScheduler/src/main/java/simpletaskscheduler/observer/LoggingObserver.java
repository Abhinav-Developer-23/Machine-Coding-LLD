package simpletaskscheduler.observer;

import java.time.LocalTime;

public class LoggingObserver implements TaskObserver {

  @Override
  public void onTaskStarted(String taskName) {
    System.out.printf("[%s] [%s] Task '%s' started.%n",
        LocalTime.now(), Thread.currentThread().getName(), taskName);
  }

  @Override
  public void onTaskCompleted(String taskName) {
    System.out.printf("[%s] [%s] Task '%s' completed successfully.%n",
        LocalTime.now(), Thread.currentThread().getName(), taskName);
  }

  @Override
  public void onTaskFailed(String taskName, int attempt, int maxAttempts, Exception e) {
    System.err.printf("[%s] [%s] Task '%s' failed on attempt %d/%d: %s%n",
        LocalTime.now(), Thread.currentThread().getName(), taskName,
        attempt, maxAttempts, e.getMessage());
  }

  @Override
  public void onTaskRetrying(String taskName, int attempt, int maxAttempts) {
    System.out.printf("[%s] [%s] Task '%s' retrying... (attempt %d/%d)%n",
        LocalTime.now(), Thread.currentThread().getName(), taskName,
        attempt + 1, maxAttempts);
  }

  @Override
  public void onTaskExhausted(String taskName, int maxAttempts) {
    System.err.printf("[%s] [%s] Task '%s' exhausted all %d attempts. Giving up.%n",
        LocalTime.now(), Thread.currentThread().getName(), taskName, maxAttempts);
  }
}
