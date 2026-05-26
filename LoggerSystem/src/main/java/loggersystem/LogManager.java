package loggersystem;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import loggersystem.strategies.LogAppender;
import lombok.Getter;

public class LogManager {
  private static final LogManager INSTANCE = new LogManager();
  private final Map<String, Logger> loggers = new ConcurrentHashMap<>();
  @Getter private final Logger rootLogger;
  private final AsyncLogProcessor processor;

  private LogManager() {
    this.rootLogger = new Logger("root");
    this.loggers.put("root", rootLogger);
    this.processor = new AsyncLogProcessor();
  }

  public static LogManager getInstance() {
    return INSTANCE;
  }

  public void shutdown() {
    // Stop the processor first to ensure all logs are written.
    processor.stop();

    // Then, close all appenders.
    loggers.values().stream()
        .flatMap(logger -> logger.getAppenders().stream())
        .distinct()
        .forEach(LogAppender::close);
    System.out.println("Logging framework shut down gracefully.");
  }

  public Logger getLogger(String name) {
    return loggers.computeIfAbsent(name, key -> new Logger(key));
  }

  AsyncLogProcessor getProcessor() {
    return processor;
  }
}
