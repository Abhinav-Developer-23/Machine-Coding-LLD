package ratelimiter.strategies;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TokenBucketStrategyTest {

  private static final String USER_A = "userA";
  private static final String USER_B = "userB";

  // ─────────────────────────────────────────────────────────────────────────
  // Basic allow / deny
  // ─────────────────────────────────────────────────────────────────────────

  @Test
  @DisplayName("First request for a new user should always be allowed")
  void firstRequestIsAllowed() {
    TokenBucketStrategy s = new TokenBucketStrategy(5, 1);
    assertTrue(s.allowRequest(USER_A));
  }

  @Test
  @DisplayName("Requests up to initial capacity should all be allowed")
  void requestsUpToCapacityAreAllowed() {
    int capacity = 4;
    TokenBucketStrategy s = new TokenBucketStrategy(capacity, 1);

    for (int i = 0; i < capacity; i++) {
      assertTrue(s.allowRequest(USER_A), "Request " + (i + 1) + " should be allowed");
    }
  }

  @Test
  @DisplayName("Request after bucket is empty should be rejected")
  void requestDeniedWhenBucketEmpty() {
    TokenBucketStrategy s = new TokenBucketStrategy(3, 1);

    s.allowRequest(USER_A); // token 3→2
    s.allowRequest(USER_A); // token 2→1
    s.allowRequest(USER_A); // token 1→0

    assertFalse(s.allowRequest(USER_A)); // bucket empty
  }

  @Test
  @DisplayName("Multiple consecutive rejections when bucket stays empty")
  void consecutiveRejectionsWhenEmpty() {
    TokenBucketStrategy s = new TokenBucketStrategy(2, 1);

    s.allowRequest(USER_A);
    s.allowRequest(USER_A);

    assertFalse(s.allowRequest(USER_A));
    assertFalse(s.allowRequest(USER_A));
    assertFalse(s.allowRequest(USER_A));
  }

  @Test
  @DisplayName("Exactly at capacity boundary — last token allowed, next denied")
  void exactlyAtCapacityBoundary() {
    int capacity = 5;
    TokenBucketStrategy s = new TokenBucketStrategy(capacity, 1);

    for (int i = 0; i < capacity; i++) {
      assertTrue(s.allowRequest(USER_A));
    }
    assertFalse(s.allowRequest(USER_A));
  }

  // ─────────────────────────────────────────────────────────────────────────
  // Refill behaviour
  // ─────────────────────────────────────────────────────────────────────────

  @Test
  @DisplayName("Bucket refills after waiting long enough, allowing more requests")
  void bucketRefillsAfterDelay() throws InterruptedException {
    // 1 token capacity, 2 tokens/second refill
    TokenBucketStrategy s = new TokenBucketStrategy(1, 2);

    assertTrue(s.allowRequest(USER_A)); // consumes the only token
    assertFalse(s.allowRequest(USER_A)); // empty

    Thread.sleep(600); // > 500ms → at least 1 token refilled

    assertTrue(s.allowRequest(USER_A)); // should be refilled
  }

  @Test
  @DisplayName("Refill does not exceed the defined capacity")
  void refillDoesNotExceedCapacity() throws InterruptedException {
    int capacity = 3;
    // 10 tokens/second refill — even after 2 seconds, bucket must cap at capacity
    TokenBucketStrategy s = new TokenBucketStrategy(capacity, 10);

    // Drain all tokens
    for (int i = 0; i < capacity; i++) s.allowRequest(USER_A);
    assertFalse(s.allowRequest(USER_A));

    Thread.sleep(2000); // refill would theoretically add 20 tokens, but cap is 3

    // Only 'capacity' tokens should be available
    for (int i = 0; i < capacity; i++) {
      assertTrue(s.allowRequest(USER_A), "Token " + (i + 1) + " should be available after refill");
    }
    assertFalse(s.allowRequest(USER_A), "No tokens beyond capacity should exist");
  }

  @Test
  @DisplayName("Partial refill: only tokens proportional to elapsed time are added")
  void partialRefill() throws InterruptedException {
    // capacity=5, 1 token/second → after 2 seconds, 2 tokens restored
    TokenBucketStrategy s = new TokenBucketStrategy(5, 1);

    // Drain all 5 tokens
    for (int i = 0; i < 5; i++) s.allowRequest(USER_A);
    assertFalse(s.allowRequest(USER_A));

    Thread.sleep(2100); // ~2 seconds → 2 tokens refilled

    assertTrue(s.allowRequest(USER_A)); // token 1
    assertTrue(s.allowRequest(USER_A)); // token 2
    assertFalse(s.allowRequest(USER_A)); // no more yet
  }

  @Test
  @DisplayName("Capacity=1 strategy: allows one, denies next, allows after refill")
  void capacityOneRefillCycle() throws InterruptedException {
    // 1 token capacity, 2 tokens/sec refill
    TokenBucketStrategy s = new TokenBucketStrategy(1, 2);

    assertTrue(s.allowRequest(USER_A)); // consumes token
    assertFalse(s.allowRequest(USER_A)); // empty

    Thread.sleep(600); // enough for 1 refill

    assertTrue(s.allowRequest(USER_A)); // refilled
    assertFalse(s.allowRequest(USER_A)); // empty again
  }

  // ─────────────────────────────────────────────────────────────────────────
  // Per-user isolation
  // ─────────────────────────────────────────────────────────────────────────

  @Test
  @DisplayName("Different users have independent buckets")
  void usersAreTrackedIndependently() {
    TokenBucketStrategy s = new TokenBucketStrategy(3, 1);

    // Drain USER_A
    s.allowRequest(USER_A);
    s.allowRequest(USER_A);
    s.allowRequest(USER_A);
    assertFalse(s.allowRequest(USER_A));

    // USER_B should still have a full bucket
    assertTrue(s.allowRequest(USER_B));
    assertTrue(s.allowRequest(USER_B));
    assertTrue(s.allowRequest(USER_B));
    assertFalse(s.allowRequest(USER_B));
  }

  @Test
  @DisplayName("Many distinct users are all tracked separately")
  void manyUsersTrackedIndependently() {
    int capacity = 4;
    TokenBucketStrategy s = new TokenBucketStrategy(capacity, 1);

    for (int u = 0; u < 10; u++) {
      String userId = "user-" + u;
      for (int r = 0; r < capacity; r++) {
        assertTrue(s.allowRequest(userId), userId + " request " + (r + 1) + " should be allowed");
      }
      assertFalse(s.allowRequest(userId), userId + " should be denied after capacity exhausted");
    }
  }

  // ─────────────────────────────────────────────────────────────────────────
  // Concurrency / thread-safety
  // ─────────────────────────────────────────────────────────────────────────

  @Test
  @DisplayName("Concurrent requests from the same user never exceed initial capacity")
  void concurrentRequestsNeverExceedCapacity() throws InterruptedException {
    int capacity = 10;
    int totalThreads = 60;
    TokenBucketStrategy s = new TokenBucketStrategy(capacity, 0); // refillRate=0 → no refill

    AtomicInteger allowed = new AtomicInteger(0);
    AtomicInteger denied = new AtomicInteger(0);

    ExecutorService executor = Executors.newFixedThreadPool(15);
    CountDownLatch latch = new CountDownLatch(totalThreads);

    for (int i = 0; i < totalThreads; i++) {
      executor.submit(
          () -> {
            if (s.allowRequest(USER_A)) allowed.incrementAndGet();
            else denied.incrementAndGet();
            latch.countDown();
          });
    }

    latch.await(5, TimeUnit.SECONDS);
    executor.shutdown();

    assertEquals(capacity, allowed.get(), "Allowed count must equal initial capacity");
    assertEquals(
        totalThreads - capacity, denied.get(), "Denied count must be totalThreads - capacity");
  }

  @Test
  @DisplayName("Concurrent requests from different users are all independently within capacity")
  void concurrentRequestsForDifferentUsersAreIsolated() throws InterruptedException {
    int capacity = 5;
    int userCount = 8;
    TokenBucketStrategy s = new TokenBucketStrategy(capacity, 0);

    List<Future<Integer>> futures = new ArrayList<>();
    ExecutorService executor = Executors.newFixedThreadPool(userCount);

    for (int u = 0; u < userCount; u++) {
      String userId = "user-" + u;
      futures.add(
          executor.submit(
              () -> {
                int allowed = 0;
                for (int r = 0; r < capacity + 3; r++) {
                  if (s.allowRequest(userId)) allowed++;
                }
                return allowed;
              }));
    }

    executor.shutdown();
    executor.awaitTermination(5, TimeUnit.SECONDS);

    for (Future<Integer> f : futures) {
      try {
        assertEquals(capacity, f.get(), "Each user should have exactly capacity requests allowed");
      } catch (ExecutionException e) {
        fail("Unexpected exception: " + e.getMessage());
      }
    }
  }
}
