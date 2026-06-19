package taskscheduler.task;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PrintMessageTask implements Task {
  private final String message;

  @Override
  public void execute() {
    System.out.printf("[%s] Executing PrintMessageTask: %s%n", LocalTime.now(), message);
  }
}
