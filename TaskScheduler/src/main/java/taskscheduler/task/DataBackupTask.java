package taskscheduler.task;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DataBackupTask implements Task {
  private final String source;
  private final String destination;

  @Override
  public void execute() {
    System.out.printf(
        "[%s] Executing DataBackupTask: Backing up from %s to %s...%n",
        LocalTime.now(), source, destination);
    // Simulate a long-running task
    System.out.printf("[%s] DataBackupTask: Backup complete.%n", LocalTime.now());
  }
}
