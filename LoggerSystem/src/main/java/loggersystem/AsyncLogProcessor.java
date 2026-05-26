package loggersystem;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import loggersystem.entities.LogMessage;
import loggersystem.strategies.LogAppender;

public class AsyncLogProcessor {
  private final ExecutorService executor;

  public AsyncLogProcessor() {

    this.executor =
        new ThreadPoolExecutor(
            1, // corePoolSize
            100, // maximumPoolSize
            60L, // keepAliveTime for idle threads beyond core
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000), // bounded queue to prevent memory issues
            new ThreadPoolExecutor
                .CallerRunsPolicy() // backpressure: caller logs directly if queue is full
            );
  }

  public void process(LogMessage logMessage, List<LogAppender> appenders) {
    if (executor.isShutdown()) {
      System.err.println("Logger is shut down. Cannot process log message.");
      return;
    }

    // Submit a new task to the executor.
    executor.submit(
        () -> {
          for (LogAppender appender : appenders) {
            appender.append(logMessage);
          }
        });
  }

  public void stop() {
    // Disable new tasks from being submitted
    executor.shutdown();
    try {
      if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
        System.err.println("Logger executor did not terminate in the specified time.");
        // Forcibly shut down any still-running tasks.
        executor.shutdownNow();
      }
    } catch (InterruptedException e) {
      executor.shutdownNow();
      // Preserve interrupt status
      Thread.currentThread().interrupt();
    }
  }
}
