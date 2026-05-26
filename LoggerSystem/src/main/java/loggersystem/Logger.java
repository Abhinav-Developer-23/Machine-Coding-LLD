package loggersystem;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import loggersystem.entities.LogMessage;
import loggersystem.enums.LogLevel;
import loggersystem.strategies.LogAppender;
import lombok.Getter;
import lombok.Setter;

public class Logger {
  private final String name;
  @Setter private LogLevel level = LogLevel.DEBUG;
  @Getter private final List<LogAppender> appenders;

  Logger(String name) {
    this.name = name;
    this.appenders = new CopyOnWriteArrayList<>();
  }

  public void addAppender(LogAppender appender) {
    appenders.add(appender);
  }

  public void log(LogLevel messageLevel, String message) {
    if (messageLevel.isGreaterOrEqual(level)) {
      LogMessage logMessage = new LogMessage(messageLevel, this.name, message);
      if (!appenders.isEmpty()) {
        LogManager.getInstance().getProcessor().process(logMessage, this.appenders);
      }
    }
  }

  public void debug(String message) {
    log(LogLevel.DEBUG, message);
  }

  public void info(String message) {
    log(LogLevel.INFO, message);
  }

  public void warn(String message) {
    log(LogLevel.WARN, message);
  }

  public void error(String message) {
    log(LogLevel.ERROR, message);
  }

  public void fatal(String message) {
    log(LogLevel.FATAL, message);
  }
}
