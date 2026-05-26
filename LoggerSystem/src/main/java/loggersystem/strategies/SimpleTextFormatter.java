package loggersystem.strategies;

import java.time.format.DateTimeFormatter;
import loggersystem.entities.LogMessage;

public class SimpleTextFormatter implements LogFormatter {
  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

  @Override
  public String format(LogMessage logMessage) {
    return String.format(
        "%s [%s] %s - %s: %s",
        logMessage.getTimestamp().format(DATE_TIME_FORMATTER),
        logMessage.getThreadName(),
        logMessage.getLevel(),
        logMessage.getLoggerName(),
        logMessage.getMessage());
  }
}
