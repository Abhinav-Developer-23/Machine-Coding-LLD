package loggersystem.strategies;

import loggersystem.entities.LogMessage;

public interface LogFormatter {
  String format(LogMessage logMessage);
}
