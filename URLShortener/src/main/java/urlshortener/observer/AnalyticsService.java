package urlshortener.observer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import urlshortener.entities.ShortenedURL;
import urlshortener.enums.EventType;

public class AnalyticsService implements Observer {
  private final Map<String, AtomicLong> clickCounts = new ConcurrentHashMap<>();

  @Override
  public void update(EventType type, ShortenedURL url) {
    switch (type) {
      case URL_CREATED:
        clickCounts.put(url.getShortKey(), new AtomicLong(0));
        System.out.printf(
            "[Analytics] URL Created: Key=%s, Original=%s%n", url.getShortKey(), url.getLongURL());
        break;
      case URL_ACCESSED:
        AtomicLong count = clickCounts.computeIfAbsent(url.getShortKey(), k -> new AtomicLong(0));
        count.incrementAndGet();
        System.out.printf(
            "[Analytics] URL Accessed: Key=%s, Clicks=%d%n", url.getShortKey(), count.get());
        break;
    }
  }
}
