package taskscheduler.strategy;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OneTimeSchedulingStrategy implements SchedulingStrategy {
  private final LocalDateTime executionTime;

  @Override
  public Optional<LocalDateTime> getNextExecutionTime(LocalDateTime lastExecutionTime) {
    // If lastExecutionTime is null, it's the first run. Otherwise, it's done.
    return (lastExecutionTime == null) ? Optional.of(executionTime) : Optional.empty();
  }
}
