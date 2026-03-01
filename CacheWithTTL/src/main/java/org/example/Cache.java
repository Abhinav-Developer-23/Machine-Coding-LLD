package org.example;

/**
 * Generic Cache interface.
 *
 * <p>Implementations must perform <b>lazy expiry cleanup</b> inside both
 * {@code get} and {@code put} so that stale entries are never returned and
 * do not consume capacity unnecessarily.
 */
public interface Cache<K, V> {

  /**
   * Retrieve the value for {@code key}.
   *
   * <p>Must evict the entry (and return {@code null}) if it has expired.
   *
   * @return the cached value, or {@code null} if absent / expired
   */
  V get(K key);

  /**
   * Store a key-value pair with no expiry (lives until evicted by capacity).
   */
  void put(K key, V value);

  /**
   * Store a key-value pair with a time-to-live.
   *
   * @param ttlMs positive milliseconds after which this entry is considered
   *              stale; {@code ≤ 0} means no expiry
   */
  void put(K key, V value, long ttlMs);

  /**
   * Explicitly remove a key from the cache.
   */
  void remove(K key);

  /**
   * Current number of <em>live</em> (non-expired) entries.
   */
  int size();
}
