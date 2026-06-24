package urlshortener.observer;

import urlshortener.entities.ShortenedURL;
import urlshortener.enums.EventType;

public interface Observer {
  void update(EventType type, ShortenedURL url);
}
