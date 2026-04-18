package org.example.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.example.enums.CrawlStatus;
import org.example.model.CrawlConfig;
import org.example.model.CrawlRequest;
import org.example.model.CrawlResult;
import org.example.observer.CrawlObserver;
import org.example.repository.CrawlRepository;
import org.example.strategy.UrlExtractor;

public class CrawlerService {

  private static final int QUEUE_CAPACITY = 50;
  private static final long KEEP_ALIVE_SECONDS = 60;

  private final UrlExtractor urlExtractor;
  private final List<CrawlObserver> observers;

  public CrawlerService(UrlExtractor urlExtractor, List<CrawlObserver> observers) {
    this.urlExtractor = urlExtractor;
    this.observers = observers;
  }

  /**
   * BFS crawl with a ThreadPoolExecutor + CompletableFuture. Processes URLs level-by-level: all
   * URLs at the current depth are submitted via supplyAsync, then allOf() waits for the entire
   * level to complete before collecting results and enqueueing children for the next level.
   *
   * <p>Uses DiscardOldestPolicy — if the work queue is full, the oldest queued task is dropped to
   * make room for the new one. This prevents the crawler from being overwhelmed when the link graph
   * explodes at deeper levels.
   */
  public void crawl(CrawlConfig config) {

    // Read thread count from config — lets caller control parallelism
    int poolSize = config.getThreadCount();

    // Create a ThreadPoolExecutor instead of Executors.newFixedThreadPool()
    // because we get full control over pool sizing, queue bounds, and rejection policy
    ThreadPoolExecutor executor =
        new ThreadPoolExecutor(
            poolSize, // corePoolSize:  minimum threads always alive
            poolSize * 2, // maxPoolSize:   can scale up under pressure (burst of URLs)
            KEEP_ALIVE_SECONDS, // keepAliveTime: idle threads beyond core are killed after 60s
            TimeUnit.SECONDS, // time unit for keepAlive
            new LinkedBlockingQueue<>(
                QUEUE_CAPACITY), // bounded queue: caps pending tasks to 50 to prevent OOM
            new ThreadPoolExecutor
                .DiscardOldestPolicy() // rejection: if queue is full, drop the oldest task (least
            // relevant)
            );

    // BFS queue — holds CrawlRequests to process, each tagged with its depth
    Queue<CrawlRequest> queue = new LinkedList<>();

    // Seed the BFS queue with initial URLs at depth 0
    for (String seedUrl : config.getSeedUrls()) {
      queue.add(new CrawlRequest(seedUrl, 0));
    }

    // BFS loop — runs until no more URLs to process
    while (!queue.isEmpty()) {

      // STEP 1: Drain ALL requests from the queue into a batch
      // This gives us the entire "current level" of BFS to process in parallel
      List<CrawlRequest> currentBatch = new ArrayList<>();
      while (!queue.isEmpty()) {
        currentBatch.add(queue.poll());
      }

      // STEP 2: Filter out already-visited URLs using atomic putIfAbsent
      // tryMarkVisited returns true only for the FIRST caller — prevents duplicate crawls
      // This is thread-safe because ConcurrentHashMap.putIfAbsent is atomic
      List<CrawlRequest> toProcess = new ArrayList<>();
      for (CrawlRequest request : currentBatch) {
        if (CrawlRepository.tryMarkVisited(request.getUrl())) {
          toProcess.add(request);
        }
      }

      // If every URL in this batch was already visited, skip to next iteration
      if (toProcess.isEmpty()) {
        continue;
      }

      // STEP 3: Submit each URL to the thread pool via CompletableFuture
      // supplyAsync(task, executor) runs the task on OUR thread pool, not the default ForkJoinPool
      // Each future will eventually hold the CrawlResult (extracted child URLs + status)
      List<CompletableFuture<CrawlResult>> futures = new ArrayList<>();
      for (CrawlRequest request : toProcess) {
        CompletableFuture<CrawlResult> future =
            CompletableFuture.supplyAsync(() -> processUrl(request), executor);
        futures.add(future);
      }

      // STEP 4: Wait for ALL futures in this level to finish before moving to next level
      // allOf() returns a single future that completes when every input future completes
      // join() blocks the main thread until that happens — ensures level-by-level BFS order
      // We convert List → array because allOf() only accepts varargs (CompletableFuture<?>...)
      CompletableFuture<?>[] futureArray = futures.toArray(new CompletableFuture[0]);
      CompletableFuture.allOf(futureArray).join();

      // STEP 5: All futures are done — safely collect results (get() won't block now)
      // We iterate by index so we can pair each result with its parent request (for depth tracking)
      for (int i = 0; i < futures.size(); i++) {
        try {
          // get() returns immediately since allOf().join() already ensured completion
          CrawlResult result = futures.get(i).get();

          // Match result back to its parent request to know the current depth
          CrawlRequest parentRequest = toProcess.get(i);

          // Persist the result in our in-memory store
          CrawlRepository.saveCrawlResult(result);

          // Notify all registered observers (logging, analytics, etc.) — Observer pattern
          notifyObservers(result);

          // Calculate child depth — children are one level deeper than parent
          int childDepth = parentRequest.getDepth() + 1;

          // Only enqueue children if:
          //   1. The crawl was successful (no point following links from a failed page)
          //   2. Child depth is within the configured max (prevents infinite crawling)
          if (result.getStatus() == CrawlStatus.SUCCESS && childDepth <= config.getMaxDepth()) {
            for (String childUrl : result.getChildUrls()) {
              queue.add(new CrawlRequest(childUrl, childDepth));
            }
          }
        } catch (Exception e) {
          System.err.println("Error processing future: " + e.getMessage());
        }
      }
    }

    // Gracefully shut down the thread pool — no new tasks accepted, existing ones finish
    executor.shutdown();
  }

  /**
   * Single-threaded BFS crawl — no executor, no futures. Simple iterative BFS: dequeue a URL,
   * process it, enqueue children. Useful for debugging or when parallelism is not needed.
   */
  public void crawlSingleThreaded(CrawlConfig config) {
    Queue<CrawlRequest> queue = new LinkedList<>();
    for (String seedUrl : config.getSeedUrls()) {
      queue.add(new CrawlRequest(seedUrl, 0));
    }

    while (!queue.isEmpty()) {
      CrawlRequest request = queue.poll();

      // Skip if already visited
      if (!CrawlRepository.tryMarkVisited(request.getUrl())) {
        continue;
      }

      // Process the URL
      CrawlResult result = processUrl(request);
      CrawlRepository.saveCrawlResult(result);
      notifyObservers(result);

      // Enqueue children if within depth limit
      int childDepth = request.getDepth() + 1;
      if (result.getStatus() == CrawlStatus.SUCCESS && childDepth <= config.getMaxDepth()) {
        for (String childUrl : result.getChildUrls()) {
          queue.add(new CrawlRequest(childUrl, childDepth));
        }
      }
    }
  }

  private CrawlResult processUrl(CrawlRequest request) {
    try {
      List<String> childUrls = urlExtractor.extractUrls(request.getUrl());
      return new CrawlResult(request.getUrl(), childUrls, CrawlStatus.SUCCESS);
    } catch (Exception e) {
      return new CrawlResult(request.getUrl(), new ArrayList<>(), CrawlStatus.FAILED);
    }
  }

  private void notifyObservers(CrawlResult result) {
    for (CrawlObserver observer : observers) {
      observer.onPageCrawled(result);
    }
  }
}
