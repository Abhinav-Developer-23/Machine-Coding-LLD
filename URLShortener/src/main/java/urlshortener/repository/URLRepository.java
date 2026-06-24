package urlshortener.repository;

import java.util.Optional;
import urlshortener.entities.ShortenedURL;

public interface URLRepository {
  void save(ShortenedURL url);

  Optional<ShortenedURL> findByKey(String key);

  Optional<String> findKeyByLongURL(String longURL);

  long getNextId();

  boolean existsByKey(String key);
}
