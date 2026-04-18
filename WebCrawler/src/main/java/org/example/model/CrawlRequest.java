package org.example.model;

import lombok.Getter;

@Getter
public class CrawlRequest {

  private final String url;
  private final int depth;

  public CrawlRequest(String url, int depth) {
    this.url = url;
    this.depth = depth;
  }
}
