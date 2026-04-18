package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.model.CrawlConfig;
import org.example.model.CrawlResult;
import org.example.observer.CrawlObserver;
import org.example.observer.LoggingCrawlObserver;
import org.example.repository.CrawlRepository;
import org.example.service.CrawlerService;
import org.example.strategy.HtmlLinkExtractor;
import org.example.strategy.UrlExtractor;

public class CrawlerRunner {

  public static void main(String[] args) {
    // --- Build a simulated web graph ---
    Map<String, List<String>> webGraph = buildWebGraph();

    // --- Wire dependencies (Strategy + Observer) ---
    UrlExtractor extractor = new HtmlLinkExtractor(webGraph);

    List<CrawlObserver> observers = new ArrayList<>();
    observers.add(new LoggingCrawlObserver());

    CrawlerService crawlerService = new CrawlerService(extractor, observers);

    // --- Configure the crawl ---
    List<String> seeds = new ArrayList<>();
    seeds.add("https://example.com");

    CrawlConfig config = new CrawlConfig(seeds, 2, 3);

    System.out.println("========================================");
    System.out.println("   WEB CRAWLER — Starting Crawl");
    System.out.println("========================================");
    System.out.println("Seed URLs    : " + seeds);
    System.out.println("Max Depth    : " + config.getMaxDepth());
    System.out.println("Thread Pool  : " + config.getThreadCount());
    System.out.println("========================================\n");

    // --- Run the crawl ---
    crawlerService.crawl(config);

    // --- Print summary ---
    System.out.println("\n========================================");
    System.out.println("   CRAWL SUMMARY");
    System.out.println("========================================");
    List<CrawlResult> allResults = CrawlRepository.getAll();
    System.out.println("Total pages crawled: " + allResults.size());
    System.out.println();
    for (CrawlResult result : allResults) {
      System.out.println(
          "  ["
              + result.getStatus()
              + "] "
              + result.getUrl()
              + " -> "
              + result.getChildUrls().size()
              + " links found");
    }
    System.out.println("========================================");
  }

  /**
   * Simulates a web graph:
   *
   * <p>example.com ├── /about │ ├── /team │ └── /contact ├── /blog │ ├── /blog/post1 ──►
   * /blog/post2 (cross-link) │ │ ──► external.com/ref │ └── /blog/post2 ──► /blog/post1 (cycle!)
   * └── /products ├── /products/item1 └── /products/item2
   */
  private static Map<String, List<String>> buildWebGraph() {
    Map<String, List<String>> graph = new HashMap<>();

    graph.put(
        "https://example.com",
        Arrays.asList(
            "https://example.com/about",
            "https://example.com/blog",
            "https://example.com/products"));

    graph.put(
        "https://example.com/about",
        Arrays.asList("https://example.com/team", "https://example.com/contact"));

    graph.put(
        "https://example.com/blog",
        Arrays.asList("https://example.com/blog/post1", "https://example.com/blog/post2"));

    graph.put(
        "https://example.com/blog/post1",
        Arrays.asList("https://example.com/blog/post2", "https://external.com/ref"));

    graph.put("https://example.com/blog/post2", Arrays.asList("https://example.com/blog/post1"));

    graph.put(
        "https://example.com/products",
        Arrays.asList("https://example.com/products/item1", "https://example.com/products/item2"));

    // Leaf pages — no outgoing links
    graph.put("https://example.com/team", new ArrayList<>());
    graph.put("https://example.com/contact", new ArrayList<>());
    graph.put("https://example.com/products/item1", new ArrayList<>());
    graph.put("https://example.com/products/item2", new ArrayList<>());
    graph.put("https://external.com/ref", Arrays.asList("https://external.com/page1"));
    graph.put("https://external.com/page1", new ArrayList<>());

    return graph;
  }
}
