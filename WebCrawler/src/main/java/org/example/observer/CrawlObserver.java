package org.example.observer;

import org.example.model.CrawlResult;

public interface CrawlObserver {

  void onPageCrawled(CrawlResult result);
}
