package ratelimiter.strategies;

public interface RateLimitingStrategy {
  boolean allowRequest(String userId);
}
