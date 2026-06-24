package urlshortener.strategies;

public interface KeyGenerationStrategy {
  String generateKey(long id);
}
