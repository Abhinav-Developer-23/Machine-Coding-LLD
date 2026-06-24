package urlshortener.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import urlshortener.entities.ShortenedURL;

public class InMemoryURLRepository implements URLRepository {
  private final Map<String, ShortenedURL> keyToUrlMap = new ConcurrentHashMap<>();
  private final Map<String, String> longUrlToKeyMap = new ConcurrentHashMap<>();
  private final AtomicLong idCounter = new AtomicLong(1);

  @Override
  public void save(ShortenedURL url) {
    keyToUrlMap.put(url.getShortKey(), url);
    longUrlToKeyMap.put(url.getLongURL(), url.getShortKey());
  }

  @Override
  public Optional<ShortenedURL> findByKey(String key) {
    return Optional.ofNullable(keyToUrlMap.get(key));
  }

  @Override
  public Optional<String> findKeyByLongURL(String longURL) {
    return Optional.ofNullable(longUrlToKeyMap.get(longURL));
  }

  @Override
  public long getNextId() {
    return idCounter.getAndIncrement();
  }

  @Override
  public boolean existsByKey(String key) {
    return keyToUrlMap.containsKey(key);
  }
}
