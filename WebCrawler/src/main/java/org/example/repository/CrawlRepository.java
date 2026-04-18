package org.example.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.example.model.CrawlResult;

public class CrawlRepository {

  // DB 1: "visited" — tracks which URLs have been claimed for processing
  //        Set is the right choice: we only care about presence, not a value
  //        ConcurrentHashMap.newKeySet() gives us a thread-safe Set backed by ConcurrentHashMap
  private static final Set<String> visited = ConcurrentHashMap.newKeySet();

  // DB 2: "store" — holds the actual CrawlResult after a URL is fully processed
  //        Used AFTER crawling to persist results for querying
  //        Two maps because we need to mark visited BEFORE processing, but results exist only AFTER
  private static final ConcurrentHashMap<String, CrawlResult> store = new ConcurrentHashMap<>();

  /**
   * Operates on: VISITED set Atomically marks a URL as visited using Set.add(). add() returns true
   * if the element was NOT already present (first visit). add() returns false if already present
   * (skip it).
   */
  public static boolean tryMarkVisited(String url) {
    return visited.add(url);
  }

  /**
   * Operates on: STORE map Persists a CrawlResult after the URL has been fetched and links
   * extracted.
   */
  public static void saveCrawlResult(CrawlResult result) {
    store.put(result.getUrl(), result);
  }

  /** Operates on: STORE map Retrieves the crawl result for a specific URL. */
  public static CrawlResult getByUrl(String url) {
    return store.get(url);
  }

  /** Operates on: STORE map Returns all crawl results collected so far. */
  public static List<CrawlResult> getAll() {
    List<CrawlResult> results = new ArrayList<>();
    for (CrawlResult result : store.values()) {
      results.add(result);
    }
    return results;
  }

  /** Operates on: BOTH maps Resets the repository — clears visited set and result store. */
  public static void clear() {
    visited.clear();
    store.clear();
  }
}
