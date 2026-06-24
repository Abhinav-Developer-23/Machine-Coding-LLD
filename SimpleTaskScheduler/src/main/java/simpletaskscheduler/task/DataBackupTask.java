package simpletaskscheduler.task;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DataBackupTask implements Runnable {
  private final String source;
  private final String destination;

  @Override
  public void run() {
    System.out.printf(
        "[%s] DataBackupTask: Backing up from '%s' to '%s'...%n",
        LocalTime.now(), source, destination);
    try {
      Thread.sleep(500); // Simulate backup work
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Backup interrupted", e);
    }
    System.out.printf("[%s] DataBackupTask: Backup complete.%n", LocalTime.now());
  }
}
