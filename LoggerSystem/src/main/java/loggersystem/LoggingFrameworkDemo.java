package loggersystem;

import java.io.File;
import loggersystem.enums.LogLevel;
import loggersystem.strategies.ConsoleAppender;
import loggersystem.strategies.FileAppender;

public class LoggingFrameworkDemo {
  public static void main(String[] args) {
    LogManager logManager = LogManager.getInstance();
    ConsoleAppender consoleAppender = new ConsoleAppender();

    // --- 1. Console Logging Demo ---
    System.out.println("--- Console Logging Demo ---");
    Logger mainLogger = logManager.getLogger("com.example.Main");
    mainLogger.setLevel(LogLevel.INFO);
    mainLogger.addAppender(consoleAppender);

    mainLogger.info("Application starting up.");
    mainLogger.debug("This is a debug message, it should NOT appear."); // Below INFO level
    mainLogger.warn("This is a warning message.");

    // --- 2. Per-Logger Level Demo ---
    System.out.println("\n--- Per-Logger Level Demo ---");
    Logger dbLogger = logManager.getLogger("com.example.db");
    dbLogger.setLevel(LogLevel.WARN); // Only WARN and above
    dbLogger.addAppender(consoleAppender);

    dbLogger.info("This INFO message should NOT appear.");
    dbLogger.warn("Database connection pool is running low.");
    dbLogger.error("Database connection failed!");

    // --- 3. Dynamic Level Change Demo ---
    System.out.println("\n--- Dynamic Level Change Demo ---");
    System.out.println("Changing mainLogger level to DEBUG...");
    mainLogger.setLevel(LogLevel.DEBUG);
    mainLogger.debug("This debug message should now be visible.");

    // --- 4. File Appender Demo ---
    System.out.println("\n--- File Appender Demo ---");
    String tmpDir = "tmp";
    new File(tmpDir).mkdirs();
    String logFilePath = tmpDir + File.separator + "app.log";
    System.out.println("Writing logs to file: " + new File(logFilePath).getAbsolutePath());

    FileAppender fileAppender = new FileAppender(logFilePath);
    Logger fileLogger = logManager.getLogger("com.example.file");
    fileLogger.addAppender(fileAppender);
    fileLogger.addAppender(consoleAppender); // Also log to console
    fileLogger.setLevel(LogLevel.DEBUG);

    fileLogger.info("This message goes to BOTH console and file.");
    fileLogger.debug("Debug log written to file.");
    fileLogger.warn("Warning log written to file.");
    fileLogger.error("Error log written to file.");

    System.out.println("Check the log file at: " + new File(logFilePath).getAbsolutePath());

    try {
      Thread.sleep(500);
      logManager.shutdown();
    } catch (Exception e) {
      System.out.println("Caught exception");
    }
  }
}
