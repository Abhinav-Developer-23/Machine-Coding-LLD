package org.example.model;

import java.util.List;
import lombok.Getter;
import org.example.enums.CrawlStatus;

@Getter
public class CrawlResult {

  private final String url;
  private final List<String> childUrls;
  private final CrawlStatus status;

  public CrawlResult(String url, List<String> childUrls, CrawlStatus status) {
    this.url = url;
    this.childUrls = childUrls;
    this.status = status;
  }
}
