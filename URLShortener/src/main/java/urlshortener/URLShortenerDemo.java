package urlshortener;

import java.util.Optional;
import urlshortener.observer.AnalyticsService;
import urlshortener.repository.InMemoryURLRepository;
import urlshortener.strategies.Base62Strategy;

public class URLShortenerDemo {
  public static void main(String[] args) {
    URLShortenerService shortener = URLShortenerService.getInstance();

    shortener.configure("http://short.ly/", new InMemoryURLRepository(), new Base62Strategy());
    shortener.addObserver(new AnalyticsService());

    System.out.println("--- URL Shortener Service Initialized ---\n");

    String originalUrl1 =
        "https://www.verylongurl.com/with/lots/of/path/segments/and/query/params?id=123&user=test";
    System.out.println("Shortening: " + originalUrl1);
    String shortUrl1 = shortener.shorten(originalUrl1);
    System.out.println("Generated Short URL: " + shortUrl1);
    System.out.println();

    System.out.println("Shortening the same URL again...");
    String shortUrl2 = shortener.shorten(originalUrl1);
    System.out.println("Generated Short URL: " + shortUrl2);
    if (shortUrl1.equals(shortUrl2)) {
      System.out.println("SUCCESS: The system correctly returned the existing short URL.\n");
    }

    String originalUrl2 = "https://www.anotherdomain.com/page.html";
    System.out.println("Shortening: " + originalUrl2);
    String shortUrl3 = shortener.shorten(originalUrl2);
    System.out.println("Generated Short URL: " + shortUrl3);
    System.out.println();

    System.out.println("--- Resolving and Tracking Clicks ---");

    resolveAndPrint(shortener, shortUrl1);
    resolveAndPrint(shortener, shortUrl1);
    resolveAndPrint(shortener, shortUrl3);

    System.out.println("\nResolving a non-existent URL...");
    resolveAndPrint(shortener, "http://short.ly/nonexistent");
  }

  private static void resolveAndPrint(URLShortenerService shortener, String shortUrl) {
    Optional<String> resolvedUrl = shortener.resolve(shortUrl);
    if (resolvedUrl.isPresent()) {
      System.out.printf("Resolved %s -> %s%n", shortUrl, resolvedUrl.get());
    } else {
      System.out.printf("No original URL found for %s%n", shortUrl);
    }
  }
}
