package ratelimiter.strategies;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FixedWindowStrategyTest {

  private static final String USER_A = "userA";
  private static final String USER_B = "userB";

  private FixedWindowStrategy strategy;

  @BeforeEach
  void setUp() {
    // 3 requests allowed per 2-second window
    strategy = new FixedWindowStrategy(3, 2);
  }

  // ─────────────────────────────────────────────────────────────────────────
  // Basic allow / deny
  // ─────────────────────────────────────────────────────────────────────────

  @Test
  @DisplayName("First request for a new user should always be allowed")
  void firstRequestIsAllowed() {
    assertTrue(strategy.allowRequest(USER_A));
  }

  @Test
  @DisplayName("Requests up to maxRequests should all be allowed")
  void requestsWithinLimitAreAllowed() {
    assertTrue(strategy.allowRequest(USER_A)); // 1
    assertTrue(strategy.allowRequest(USER_A)); // 2
    assertTrue(strategy.allowRequest(USER_A)); // 3
  }

  @Test
  @DisplayName("Request exceeding maxRequests should be rejected")
  void requestBeyondLimitIsRejected() {
    strategy.allowRequest(USER_A); // 1
    strategy.allowRequest(USER_A); // 2
    strategy.allowRequest(USER_A); // 3 — hits the cap

    assertFalse(strategy.allowRequest(USER_A)); // 4 — must be denied
  }

  @Test
  @DisplayName("Subsequent requests beyond limit are all rejected until window resets")
  void multipleRequestsBeyondLimitAreAllRejected() {
    strategy.allowRequest(USER_A);
    strategy.allowRequest(USER_A);
    strategy.allowRequest(USER_A);

    assertFalse(strategy.allowRequest(USER_A));
    assertFalse(strategy.allowRequest(USER_A));
    assertFalse(strategy.allowRequest(USER_A));
  }

  @Test
  @DisplayName("Exactly maxRequests requests should be allowed, the next one denied")
  void exactlyAtLimitBoundary() {
    int maxRequests = 5;
    FixedWindowStrategy s = new FixedWindowStrategy(maxRequests, 10);

    for (int i = 0; i < maxRequests; i++) {
      assertTrue(s.allowRequest(USER_A), "Request " + (i + 1) + " should be allowed");
    }
    assertFalse(s.allowRequest(USER_A), "Request " + (maxRequests + 1) + " should be denied");
  }

  // ─────────────────────────────────────────────────────────────────────────
  // Window reset
  // ─────────────────────────────────────────────────────────────────────────

  @Test
  @DisplayName("Counter resets after the window expires, allowing requests again")
  void windowResetsAfterExpiry() throws InterruptedException {
    // 2 requests per 1-second window
    FixedWindowStrategy s = new FixedWindowStrategy(2, 1);

    assertTrue(s.allowRequest(USER_A)); // 1
    assertTrue(s.allowRequest(USER_A)); // 2
    assertFalse(s.allowRequest(USER_A)); // denied

    // Wait for the window to expire
    Thread.sleep(1100);

    // Counter should have reset — new window starts
    assertTrue(s.allowRequest(USER_A));
  }

  @Test
  @DisplayName("After window reset the full quota is available again")
  void fullQuotaAvailableAfterReset() throws InterruptedException {
    FixedWindowStrategy s = new FixedWindowStrategy(3, 1);

    s.allowRequest(USER_A);
    s.allowRequest(USER_A);
    s.allowRequest(USER_A);

    Thread.sleep(1100); // let the window expire

    // All 3 slots should be fresh
    assertTrue(s.allowRequest(USER_A)); // 1
    assertTrue(s.allowRequest(USER_A)); // 2
    assertTrue(s.allowRequest(USER_A)); // 3
    assertFalse(s.allowRequest(USER_A)); // 4 — denied again
  }

  // ─────────────────────────────────────────────────────────────────────────
  // Per-user isolation
  // ─────────────────────────────────────────────────────────────────────────

  @Test
  @DisplayName("Different users have independent windows — one user's limit doesn't affect another")
  void usersAreTrackedIndependently() {
    // Exhaust USER_A
    strategy.allowRequest(USER_A);
    strategy.allowRequest(USER_A);
    strategy.allowRequest(USER_A);
    assertFalse(strategy.allowRequest(USER_A));

    // USER_B should still have its full quota
    assertTrue(strategy.allowRequest(USER_B));
    assertTrue(strategy.allowRequest(USER_B));
    assertTrue(strategy.allowRequest(USER_B));
    assertFalse(strategy.allowRequest(USER_B));
  }

  @Test
  @DisplayName("Many distinct users are all tracked separately")
  void manyUsersTrackedIndependently() {
    int maxRequests = 3;
    FixedWindowStrategy s = new FixedWindowStrategy(maxRequests, 10);

    for (int u = 0; u < 10; u++) {
      String userId = "user-" + u;
      for (int r = 0; r < maxRequests; r++) {
        assertTrue(s.allowRequest(userId), userId + " request " + (r + 1) + " should be allowed");
      }
      assertFalse(s.allowRequest(userId), userId + " should be denied after " + maxRequests);
    }
  }

  // ─────────────────────────────────────────────────────────────────────────
  // maxRequests = 1 edge case
  // ─────────────────────────────────────────────────────────────────────────

  @Test
  @DisplayName("With maxRequests=1, first request is allowed and second is denied")
  void maxRequestsOfOne() {
    FixedWindowStrategy s = new FixedWindowStrategy(1, 10);
    assertTrue(s.allowRequest(USER_A));
    assertFalse(s.allowRequest(USER_A));
  }

  // ─────────────────────────────────────────────────────────────────────────
  // Concurrency / thread-safety
  // ─────────────────────────────────────────────────────────────────────────

  @Test
  @DisplayName("Concurrent requests from the same user never exceed maxRequests allowed count")
  void concurrentRequestsNeverExceedLimit() throws InterruptedException {
    int maxRequests = 10;
    int totalThreads = 50;
    FixedWindowStrategy s = new FixedWindowStrategy(maxRequests, 60);

    AtomicInteger allowed = new AtomicInteger(0);
    AtomicInteger denied = new AtomicInteger(0);

    ExecutorService executor = Executors.newFixedThreadPool(10);
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

    assertEquals(
        maxRequests, allowed.get(), "Exactly maxRequests threads should have been allowed");
    assertEquals(
        totalThreads - maxRequests, denied.get(), "Remaining threads should have been denied");
  }

  @Test
  @DisplayName("Concurrent requests from different users are all independently within their limits")
  void concurrentRequestsForDifferentUsersAreIsolated() throws InterruptedException {
    int maxRequests = 5;
    int userCount = 10;
    FixedWindowStrategy s = new FixedWindowStrategy(maxRequests, 60);

    List<Future<Integer>> futures = new ArrayList<>();
    ExecutorService executor = Executors.newFixedThreadPool(userCount);

    for (int u = 0; u < userCount; u++) {
      String userId = "user-" + u;
      futures.add(
          executor.submit(
              () -> {
                int allowed = 0;
                // Each user fires maxRequests + 2 times
                for (int r = 0; r < maxRequests + 2; r++) {
                  if (s.allowRequest(userId)) allowed++;
                }
                return allowed;
              }));
    }

    executor.shutdown();
    executor.awaitTermination(5, TimeUnit.SECONDS);

    for (Future<Integer> f : futures) {
      try {
        assertEquals(maxRequests, f.get(), "Each user should have exactly maxRequests allowed");
      } catch (ExecutionException e) {
        fail("Unexpected exception: " + e.getMessage());
      }
    }
  }
}
