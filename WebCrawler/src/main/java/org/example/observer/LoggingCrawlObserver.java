package org.example.observer;

import org.example.model.CrawlResult;

public class LoggingCrawlObserver implements CrawlObserver {

  @Override
  public void onPageCrawled(CrawlResult result) {
    System.out.println(
        "["
            + Thread.currentThread().getName()
            + "] "
            + "Crawled: "
            + result.getUrl()
            + " | Status: "
            + result.getStatus()
            + " | Links found: "
            + result.getChildUrls().size());
  }
}
