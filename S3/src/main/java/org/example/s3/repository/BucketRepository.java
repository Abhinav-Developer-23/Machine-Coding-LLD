package org.example.s3.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.example.s3.model.Bucket;

public class BucketRepository {
  private static final BucketRepository INSTANCE = new BucketRepository();
  private static final ConcurrentHashMap<String, Bucket> BUCKETS = new ConcurrentHashMap<>();

  private BucketRepository() {}

  public static BucketRepository getInstance() {
    return INSTANCE;
  }

  public Bucket save(Bucket bucket) {
    Bucket existing = BUCKETS.putIfAbsent(bucket.getName(), bucket);
    if (existing != null) {
      throw new IllegalArgumentException("Bucket already exists: " + bucket.getName());
    }
    return bucket;
  }

  public Bucket findByName(String bucketName) {
    return BUCKETS.get(bucketName);
  }

  public List<Bucket> findAll() {
    return new ArrayList<>(BUCKETS.values());
  }

  public void delete(String bucketName) {
    BUCKETS.remove(bucketName);
  }
}
