package org.example.s3.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.example.s3.model.Bucket;

/**
 * In-memory repository managing storage, retrieval, and deletion of {@link Bucket} entities.
 * Implemented as a thread-safe singleton.
 */
public class BucketRepository {
  private static final BucketRepository INSTANCE = new BucketRepository();
  private static final ConcurrentHashMap<String, Bucket> BUCKETS = new ConcurrentHashMap<>();

  /** Private constructor to enforce the singleton design pattern. */
  private BucketRepository() {}

  /**
   * Retrieves the global singleton instance of this repository.
   *
   * @return the singleton BucketRepository instance
   */
  public static BucketRepository getInstance() {
    return INSTANCE;
  }

  /**
   * Saves a new bucket to the repository.
   *
   * @param bucket the Bucket entity to save
   * @return the saved Bucket entity
   * @throws IllegalArgumentException if a bucket with the same name already exists
   */
  public Bucket save(Bucket bucket) {
    Bucket existing = BUCKETS.putIfAbsent(bucket.getName(), bucket);
    if (existing != null) {
      throw new IllegalArgumentException("Bucket already exists: " + bucket.getName());
    }
    return bucket;
  }

  /**
   * Finds a bucket by its name.
   *
   * @param bucketName the name of the bucket to find
   * @return the Bucket entity, or null if not found
   */
  public Bucket findByName(String bucketName) {
    return BUCKETS.get(bucketName);
  }

  /**
   * Retrieves all buckets currently stored in the repository.
   *
   * @return a list containing all stored Bucket entities
   */
  public List<Bucket> findAll() {
    return new ArrayList<>(BUCKETS.values());
  }

  /**
   * Deletes a bucket from the repository by its name.
   *
   * @param bucketName the name of the bucket to delete
   */
  public void delete(String bucketName) {
    BUCKETS.remove(bucketName);
  }
}
