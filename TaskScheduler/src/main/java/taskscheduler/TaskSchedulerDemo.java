package taskscheduler;

import java.time.Duration;
import java.time.LocalDateTime;
import taskscheduler.observer.LoggingObserver;
import taskscheduler.service.TaskSchedulerService;
import taskscheduler.strategy.OneTimeSchedulingStrategy;
import taskscheduler.strategy.RecurringSchedulingStrategy;
import taskscheduler.strategy.SchedulingStrategy;
import taskscheduler.task.DataBackupTask;
import taskscheduler.task.FailingTask;
import taskscheduler.task.PrintMessageTask;
import taskscheduler.task.Task;

public class TaskSchedulerDemo {
  public static void main(String[] args) throws InterruptedException {
    // 1. Setup the facade and observers
    TaskSchedulerService scheduler = TaskSchedulerService.getInstance();
    scheduler.addObserver(new LoggingObserver());

    // 2. Initialize the scheduler
    scheduler.initialize(10);

    // 3. Define tasks and strategies
    // Scenario 1: One-time task, 1 second from now
    Task oneTimeTask = new PrintMessageTask("This is a one-time task.");
    SchedulingStrategy oneTimeStrategy =
        new OneTimeSchedulingStrategy(LocalDateTime.now().plusSeconds(1));

    // Scenario 2: Recurring task, every 2 seconds
    Task recurringTask = new PrintMessageTask("This is a recurring task.");
    SchedulingStrategy recurringStrategy = new RecurringSchedulingStrategy(Duration.ofSeconds(2));

    // Scenario 3: A long-running backup task, scheduled to run in 3 seconds
    Task backupTask = new DataBackupTask("/data/source", "/data/backup");
    SchedulingStrategy longRunningRecurringStrategy =
        new OneTimeSchedulingStrategy(LocalDateTime.now().plusSeconds(3));

    // Scenario 4: A task that fails 2 times then succeeds (with 3 retries allowed)
    Task failThenSucceedTask = new FailingTask("RecoverableTask", 2);
    SchedulingStrategy failStrategy =
        new OneTimeSchedulingStrategy(LocalDateTime.now().plusSeconds(1));

    // Scenario 5: A task that always fails (exhausts all retries)
    Task alwaysFailTask = new FailingTask("HopelessTask", 100);
    SchedulingStrategy alwaysFailStrategy =
        new OneTimeSchedulingStrategy(LocalDateTime.now().plusSeconds(2));

    // 4. Schedule the tasks using the facade
    System.out.println("Scheduling tasks...");
    scheduler.schedule(oneTimeTask, oneTimeStrategy);
    scheduler.schedule(recurringTask, recurringStrategy);
    scheduler.schedule(backupTask, longRunningRecurringStrategy);
    scheduler.schedule(failThenSucceedTask, failStrategy, 3);   // 3 retries allowed
    scheduler.schedule(alwaysFailTask, alwaysFailStrategy, 2);  // 2 retries allowed

    // 5. Let the demo run for a while
    System.out.println(
        "Scheduler is running. Waiting for tasks to execute... (Demo will run for 8 seconds)");
    Thread.sleep(8000);

    // 6. Shutdown the scheduler
    scheduler.shutdown();
  }
}
