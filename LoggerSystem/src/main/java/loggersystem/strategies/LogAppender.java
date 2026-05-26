package loggersystem.strategies;

import loggersystem.entities.LogMessage;

public interface LogAppender {
  void append(LogMessage logMessage);

  void close();

  LogFormatter getFormatter();

  void setFormatter(LogFormatter formatter);
}
