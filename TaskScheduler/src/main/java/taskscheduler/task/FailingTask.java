package taskscheduler.task;

import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;

@Getter
public class FailingTask implements Task {
  private final String name;
  private final int failCount;
  private final AtomicInteger attempts = new AtomicInteger(0);

  /**
   * A task that fails for the first {@code failCount} attempts and succeeds afterwards.
   *
   * @param name descriptive name for logging
   * @param failCount number of times to throw before succeeding
   */
  public FailingTask(String name, int failCount) {
    this.name = name;
    this.failCount = failCount;
  }

  @Override
  public void execute() {
    int attempt = attempts.incrementAndGet();
    if (attempt <= failCount) {
      System.out.printf(
          "[%s] FailingTask '%s': attempt %d — throwing exception!%n",
          LocalTime.now(), name, attempt);
      throw new RuntimeException("Simulated failure on attempt " + attempt);
    }
    System.out.printf(
        "[%s] FailingTask '%s': attempt %d — SUCCESS!%n", LocalTime.now(), name, attempt);
  }
}
