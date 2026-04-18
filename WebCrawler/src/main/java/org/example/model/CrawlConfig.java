package org.example.model;

import java.util.List;
import lombok.Getter;

@Getter
public class CrawlConfig {

  private final List<String> seedUrls;
  private final int maxDepth;
  private final int threadCount;

  public CrawlConfig(List<String> seedUrls, int maxDepth, int threadCount) {
    this.seedUrls = seedUrls;
    this.maxDepth = maxDepth;
    this.threadCount = threadCount;
  }
}
