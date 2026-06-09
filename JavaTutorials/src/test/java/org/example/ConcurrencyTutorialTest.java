package org.example;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * ConcurrencyTutorialTest provides comprehensive examples, explanations, and assertions for the
 * Java Concurrency Utilities: ExecutorService, ThreadPoolExecutor, and CompletableFuture.
 *
 * <p>Each test method functions as a standalone tutorial demonstrating specific aspects of these
 * APIs, accompanied by detailed inline explanations.
 */
public class ConcurrencyTutorialTest {

  /** Helper method to simulate a time-consuming task */
  private void sleep(long milliseconds) {
    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }

  /**
   * ========================================================================= SECTION 1: EXECUTOR
   * SERVICE EXAMPLES =========================================================================
   */
  @Test
  @DisplayName("ExecutorService: Basic lifecycle and task submission")
  public void testExecutorServiceLifecycle() throws ExecutionException, InterruptedException {
    /**
     * 1. Creation: Executors utility class provides predefined thread pool configurations. Here, we
     * create a fixed-size thread pool with 2 threads.
     */
    ExecutorService executor = Executors.newFixedThreadPool(2);

    /** 2. Submit a Runnable (does not return a result) */
    Future<?> runnableFuture =
        executor.submit(
            () -> {
              System.out.println("Runnable task executing in: " + Thread.currentThread().getName());
              sleep(100);
            });

    /** 3. Submit a Callable (returns a result and can throw checked exceptions) */
    Future<String> callableFuture =
        executor.submit(
            () -> {
              System.out.println("Callable task executing in: " + Thread.currentThread().getName());
              sleep(150);
              return "Task Result";
            });

    /** 4. Checking status and retrieving results Future.get() blocks until the task completes */
    assertNull(runnableFuture.get());
    /** Runnable future returns null upon successful execution */
    assertFalse(runnableFuture.isCancelled());
    assertTrue(runnableFuture.isDone());

    String result = callableFuture.get();
    /** Blocks until Callable finishes */
    assertEquals("Task Result", result);
    assertTrue(callableFuture.isDone());

    /**
     * 5. Proper Shutdown sequence shutdown() initiates an orderly shutdown in which previously
     * submitted tasks are executed, but no new tasks will be accepted.
     */
    executor.shutdown();

    /** Verifying executor is shutting down and won't accept new tasks */
    assertThrows(
        RejectedExecutionException.class,
        () -> {
          executor.submit(() -> System.out.println("This should fail"));
        });

    /**
     * awaitTermination() blocks until all tasks have completed execution after a shutdown request,
     * or the timeout occurs, or the current thread is interrupted, whichever happens first.
     */
    boolean terminated = executor.awaitTermination(2, TimeUnit.SECONDS);
    assertTrue(terminated, "Executor should have terminated successfully");
  }

  @Test
  @DisplayName("ExecutorService: Bulk operations (invokeAny and invokeAll)")
  public void testExecutorServiceBulkOperations() throws InterruptedException, ExecutionException {
    ExecutorService executor = Executors.newCachedThreadPool();

    List<Callable<String>> tasks =
        Arrays.asList(
            () -> {
              sleep(200);
              return "Task A";
            },
            () -> {
              sleep(100);
              return "Task B";
            },
            () -> {
              sleep(300);
              return "Task C";
            });

    /**
     * 1. invokeAny() Executes the given tasks, returning the result of one that has completed
     * successfully (i.e., without throwing an exception), if any do. It cancels the remaining
     * tasks.
     */
    String fastestResult = executor.invokeAny(tasks);
    assertEquals(
        "Task B", fastestResult, "invokeAny should return the fastest successful task result");

    /**
     * 2. invokeAll() Executes the given tasks, returning a list of Futures holding their status and
     * results when all complete. Note that Future.isDone() is true for each element of the returned
     * list.
     */
    List<Future<String>> futures = executor.invokeAll(tasks);

    List<String> results =
        futures.stream()
            .map(
                f -> {
                  try {
                    return f.get();
                  } catch (Exception e) {
                    throw new RuntimeException(e);
                  }
                })
            .collect(Collectors.toList());

    assertEquals(Arrays.asList("Task A", "Task B", "Task C"), results);

    executor.shutdown();
  }

  /**
   * ========================================================================= SECTION 2: THREAD
   * POOL EXECUTOR EXAMPLES
   * =========================================================================
   */
  @Test
  @DisplayName("ThreadPoolExecutor: Detailed custom configuration and rejection policies")
  public void testThreadPoolExecutorConfiguration() {
    /**
     * Let's create a custom ThreadPoolExecutor to understand all its parameters: - corePoolSize
     * (2): Minimum number of threads to keep in the pool, even if idle. - maximumPoolSize (4):
     * Maximum number of threads allowed in the pool. - keepAliveTime (100): When number of threads
     * is > core, this is the max time idle threads wait for new tasks. - unit
     * (TimeUnit.MILLISECONDS): Time unit for keepAliveTime. - workQueue (ArrayBlockingQueue size
     * 2): Queue used for holding tasks before execution. - threadFactory: Custom thread factory to
     * name our threads. - handler: Rejection handler to manage tasks that cannot be accepted.
     */
    int corePoolSize = 2;
    int maxPoolSize = 4;
    long keepAliveTime = 100;
    BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(2);

    /** Custom ThreadFactory to name threads and mark them as daemon if needed */
    ThreadFactory threadFactory =
        new ThreadFactory() {
          private final AtomicInteger counter = new AtomicInteger(1);

          @Override
          public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "custom-pool-thread-" + counter.getAndIncrement());
            t.setDaemon(false);
            return t;
          }
        };

    /** Rejection Policy: Let's use AbortPolicy which throws RejectedExecutionException */
    RejectedExecutionHandler abortHandler = new ThreadPoolExecutor.AbortPolicy();

    ThreadPoolExecutor threadPool =
        new ThreadPoolExecutor(
            corePoolSize,
            maxPoolSize,
            keepAliveTime,
            TimeUnit.MILLISECONDS,
            queue,
            threadFactory,
            abortHandler);

    /**
     * Under high load, task execution behavior follows this lifecycle: 1. Submit up to corePoolSize
     * (2) tasks -> immediately runs on new core threads. 2. Submit next tasks -> placed into queue
     * (up to queue capacity = 2). 3. Submit further tasks -> pool spawns new threads up to
     * maxPoolSize (4). 4. Submit even more tasks -> tasks exceed (maxPoolSize + queue capacity) = 6
     * tasks -> Rejection Policy.
     */

    /** Let's verify this behavior: */

    /** Submit 2 core tasks (they block to let us observe pool behavior) */
    CountDownLatch latch = new CountDownLatch(1);
    for (int i = 0; i < 2; i++) {
      threadPool.submit(
          () -> {
            try {
              latch.await();
              /** Hold threads occupied */
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            }
          });
    }
    assertEquals(2, threadPool.getPoolSize());
    assertEquals(0, threadPool.getQueue().size());

    /** Submit 2 queue tasks */
    for (int i = 0; i < 2; i++) {
      threadPool.submit(() -> {});
    }
    assertEquals(2, threadPool.getPoolSize());
    assertEquals(2, threadPool.getQueue().size());
    /** Queue is now full (capacity 2) */

    /** Submit 2 extra tasks (pool grows from corePoolSize to maxPoolSize) */
    for (int i = 0; i < 2; i++) {
      threadPool.submit(
          () -> {
            try {
              latch.await();
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            }
          });
    }
    assertEquals(4, threadPool.getPoolSize());
    /** Spawns up to maximumPoolSize */
    assertEquals(2, threadPool.getQueue().size());
    /** Queue remains full */

    /** The next submission must throw RejectedExecutionException */
    assertThrows(
        RejectedExecutionException.class,
        () -> {
          threadPool.submit(() -> {});
        });

    /** Release the latch so all threads can finish their work */
    latch.countDown();
    threadPool.shutdown();
  }

  @Test
  @DisplayName("ThreadPoolExecutor: Alternative rejection policies")
  public void testRejectionPolicies() {
    int corePoolSize = 1;
    int maxPoolSize = 1;
    BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1);

    /** 1. CallerRunsPolicy: Runs the task in the caller thread (here, the main test thread) */
    ThreadPoolExecutor callerRunsExecutor =
        new ThreadPoolExecutor(
            corePoolSize,
            maxPoolSize,
            0L,
            TimeUnit.MILLISECONDS,
            queue,
            new ThreadPoolExecutor.CallerRunsPolicy());

    /** Fill up executor and queue */
    callerRunsExecutor.submit(() -> sleep(200));
    /** core thread busy */
    callerRunsExecutor.submit(() -> sleep(200));
    /** queue full */

    /**
     * This submission exceeds capacity. CallerRunsPolicy runs it in the caller (main test) thread.
     */
    long start = System.currentTimeMillis();
    callerRunsExecutor.submit(
        () -> {
          sleep(100);
          System.out.println("Executed by thread: " + Thread.currentThread().getName());
        });
    long duration = System.currentTimeMillis() - start;
    /** Verify that it executed in the caller thread, taking at least 100ms */
    assertTrue(duration >= 100, "Should run in the calling thread synchronously");
    callerRunsExecutor.shutdown();

    /** 2. DiscardPolicy: Silently discards the rejected task */
    ThreadPoolExecutor discardExecutor =
        new ThreadPoolExecutor(
            corePoolSize,
            maxPoolSize,
            0L,
            TimeUnit.MILLISECONDS,
            queue,
            new ThreadPoolExecutor.DiscardPolicy());
    discardExecutor.submit(() -> sleep(200));
    /** core thread busy */
    discardExecutor.submit(() -> sleep(200));
    /** queue full */

    /** This submit is silently discarded. No exception is thrown. */
    Future<?> discardedFuture = discardExecutor.submit(() -> System.out.println("Never runs"));
    assertNotNull(discardedFuture);
    discardExecutor.shutdown();

    /**
     * 3. DiscardOldestPolicy: Discards the oldest unhandled request in the queue and retries submit
     */
    ThreadPoolExecutor discardOldestExecutor =
        new ThreadPoolExecutor(
            corePoolSize,
            maxPoolSize,
            0L,
            TimeUnit.MILLISECONDS,
            queue,
            new ThreadPoolExecutor.DiscardOldestPolicy());
    discardOldestExecutor.submit(() -> sleep(300));
    /** core thread busy */

    /** Submit Task 1 (placed in queue) */
    discardOldestExecutor.submit(() -> System.out.println("Task 1"));
    /** Submit Task 2 (rejection occurs -> discards Task 1 from queue, places Task 2 in queue) */
    discardOldestExecutor.submit(() -> System.out.println("Task 2"));

    discardOldestExecutor.shutdown();
  }

  /**
   * ========================================================================= SECTION 3:
   * COMPLETABLEFUTURE EXAMPLES
   * =========================================================================
   */
  @Test
  @DisplayName("CompletableFuture: Basic asynchronous task creation")
  public void testCompletableFutureCreation() throws ExecutionException, InterruptedException {
    /** 1. supplyAsync() - Executes task asynchronously and returns a result */
    CompletableFuture<String> supplierFuture =
        CompletableFuture.supplyAsync(
            () -> {
              sleep(50);
              return "Hello from " + Thread.currentThread().getName();
            });

    /**
     * 2. runAsync() - Executes task asynchronously and returns CompletableFuture<Void> (no result)
     */
    CompletableFuture<Void> runnerFuture =
        CompletableFuture.runAsync(
            () -> {
              sleep(50);
              System.out.println(
                  "Side-effect executed asynchronously in " + Thread.currentThread().getName());
            });

    /** Verify supplyAsync return value */
    String result = supplierFuture.get();
    assertTrue(result.contains("ForkJoinPool.commonPool") || result.contains("Thread-"));

    /** Verify runAsync completes successfully */
    assertNull(runnerFuture.get());
  }

  @Test
  @DisplayName(
      "CompletableFuture: Transforming and consuming results (thenApply, thenAccept, thenRun)")
  public void testCompletableFutureChaining() throws ExecutionException, InterruptedException {
    /**
     * thenApply() represents map: T -> U thenAccept() represents consumption: T -> void thenRun()
     * represents side-effect trigger: void -> void
     */
    CompletableFuture<String> chain =
        CompletableFuture.supplyAsync(() -> "hello")
            /** Apply transformation */
            .thenApply(val -> val + " world")
            /** Transform again to uppercase */
            .thenApply(String::toUpperCase);

    assertEquals("HELLO WORLD", chain.get());

    /** thenAccept() - consumes the final output */
    CompletableFuture<Void> acceptFuture =
        chain.thenAccept(
            result -> {
              System.out.println("Consumed result: " + result);
              assertEquals("HELLO WORLD", result);
            });
    acceptFuture.get();

    /** thenRun() - runs a Runnable after completion, doesn't get or return any values */
    AtomicInteger counter = new AtomicInteger(0);
    CompletableFuture<Void> runFuture =
        acceptFuture.thenRun(
            () -> {
              counter.incrementAndGet();
              System.out.println("Finished processing entire pipeline.");
            });
    runFuture.get();

    assertEquals(1, counter.get());
  }

  @Test
  @DisplayName("CompletableFuture: Combining independent futures (thenCombine)")
  public void testCompletableFutureThenCombine() throws ExecutionException, InterruptedException {
    /**
     * thenCombine combines two independent futures in parallel and executes a BiFunction when both
     * complete.
     */
    CompletableFuture<Double> usdPriceFuture =
        CompletableFuture.supplyAsync(
            () -> {
              sleep(50);
              return 10.0;
            });

    CompletableFuture<Double> exchangeRateFuture =
        CompletableFuture.supplyAsync(
            () -> {
              sleep(30);
              return 83.0;
              /** 1 USD = 83 INR */
            });

    CompletableFuture<Double> inrPriceFuture =
        usdPriceFuture.thenCombine(exchangeRateFuture, (price, rate) -> price * rate);

    assertEquals(830.0, inrPriceFuture.get());
  }

  @Test
  @DisplayName("CompletableFuture: Chaining dependent futures (thenCompose)")
  public void testCompletableFutureThenCompose() throws ExecutionException, InterruptedException {
    /**
     * thenCompose is like flatMap: T -> CompletableFuture<U>. It is used when the next step itself
     * returns a CompletableFuture, avoiding nested CompletableFutures.
     */
    CompletableFuture<String> userFuture = CompletableFuture.supplyAsync(() -> "user_101");

    /** Helper function that simulates database lookup and returns a Future */
    java.util.function.Function<String, CompletableFuture<String>> getEmailFuture =
        userId -> CompletableFuture.supplyAsync(() -> userId + "@example.com");

    /**
     * Using thenCompose to flatten the result CompletableFuture<CompletableFuture<String>> ->
     * CompletableFuture<String>
     */
    CompletableFuture<String> emailFuture = userFuture.thenCompose(getEmailFuture);

    assertEquals("user_101@example.com", emailFuture.get());
  }

  @Test
  @DisplayName("CompletableFuture: Exception handling methods")
  public void testCompletableFutureExceptionHandling()
      throws ExecutionException, InterruptedException {
    /** 1. exceptionally() - Recovers from an exception by providing a default value. */
    CompletableFuture<String> exceptionallyFuture =
        CompletableFuture.supplyAsync(
                () -> {
                  if (true) {
                    throw new IllegalArgumentException("Database connection failed");
                  }
                  return "Data";
                })
            .exceptionally(
                ex -> {
                  System.out.println("Exception caught: " + ex.getMessage());
                  return "Fallback Data";
                  /** Return fallback value */
                });

    assertEquals("Fallback Data", exceptionallyFuture.get());

    /**
     * 2. handle() - Always executes. Receives both the result and the exception. Can transform both
     * success or error results.
     */
    CompletableFuture<String> handleFuture =
        CompletableFuture.supplyAsync(
                () -> {
                  if (true) {
                    throw new RuntimeException("Calculations error");
                  }
                  return "Success Result";
                })
            .handle(
                (result, exception) -> {
                  if (exception != null) {
                    return "Handled: " + exception.getMessage();
                  }
                  return result.toUpperCase();
                });

    assertTrue(handleFuture.get().contains("Handled:"));

    /**
     * 3. whenComplete() - Side effect callback, does not alter the return value. It passes the
     * exception along to downstream stages.
     */
    CompletableFuture<String> whenCompleteFuture =
        CompletableFuture.supplyAsync(() -> "Original Value");

    CompletableFuture<String> downstream =
        whenCompleteFuture.whenComplete(
            (result, exception) -> {
              System.out.println("Completed with value: " + result);
              /** Side effects here (logging, resource cleanup) */
            });

    assertEquals("Original Value", downstream.get());
  }

  @Test
  @DisplayName("CompletableFuture: Working with multiple futures (allOf and anyOf)")
  public void testMultipleFuturesAllOfAndAnyOf() throws ExecutionException, InterruptedException {
    /** 1. CompletableFuture.allOf() - returns when all of the futures have completed. */
    CompletableFuture<String> f1 =
        CompletableFuture.supplyAsync(
            () -> {
              sleep(20);
              return "Result 1";
            });
    CompletableFuture<String> f2 =
        CompletableFuture.supplyAsync(
            () -> {
              sleep(40);
              return "Result 2";
            });
    CompletableFuture<String> f3 =
        CompletableFuture.supplyAsync(
            () -> {
              sleep(10);
              return "Result 3";
            });

    CompletableFuture<Void> allOfFuture = CompletableFuture.allOf(f1, f2, f3);

    /** Wait for all to finish */
    allOfFuture.get();

    /** Retrieve and combine results */
    String combined =
        Stream.of(f1, f2, f3)
            .map(CompletableFuture::join)
            /** join() is like get() but throws unchecked exceptions */
            .collect(Collectors.joining(", "));

    assertEquals("Result 1, Result 2, Result 3", combined);

    /** 2. CompletableFuture.anyOf() - returns when any one of the futures completes. */
    CompletableFuture<String> slow =
        CompletableFuture.supplyAsync(
            () -> {
              sleep(100);
              return "Slow Task";
            });
    CompletableFuture<String> fast =
        CompletableFuture.supplyAsync(
            () -> {
              sleep(10);
              return "Fast Task";
            });

    CompletableFuture<Object> anyOfFuture = CompletableFuture.anyOf(slow, fast);

    assertEquals("Fast Task", anyOfFuture.get());
  }

  @Test
  @DisplayName("CompletableFuture: Manual Completion")
  public void testManualCompletion() throws ExecutionException, InterruptedException {
    CompletableFuture<String> future = new CompletableFuture<>();

    /** Start a thread that will complete the future after some time */
    new Thread(
            () -> {
              sleep(50);
              future.complete("Manual Value");
            })
        .start();

    /** get() blocks until complete() is called */
    assertEquals("Manual Value", future.get());

    /** completeExceptionally allows failing the future manually */
    CompletableFuture<String> failedFuture = new CompletableFuture<>();
    failedFuture.completeExceptionally(new RuntimeException("Manual failure"));

    assertThrows(ExecutionException.class, failedFuture::get);
  }
}
