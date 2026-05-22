package org.example.s3.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.example.s3.model.S3Object;

/**
 * In-memory repository managing storage, retrieval, and deletion of {@link S3Object} entities.
 * Implemented as a thread-safe singleton with compounded keys (bucketName/key).
 */
public class S3ObjectRepository {
  private static final S3ObjectRepository INSTANCE = new S3ObjectRepository();
  private static final ConcurrentHashMap<String, S3Object> OBJECTS = new ConcurrentHashMap<>();

  /**
   * Private constructor to enforce the singleton design pattern.
   */
  private S3ObjectRepository() {}

  /**
   * Retrieves the global singleton instance of this repository.
   *
   * @return the singleton S3ObjectRepository instance
   */
  public static S3ObjectRepository getInstance() {
    return INSTANCE;
  }

  /**
   * Saves a new or modified S3Object to the repository.
   *
   * @param object the S3Object entity to save
   * @return the saved S3Object entity
   */
  public S3Object save(S3Object object) {
    OBJECTS.put(objectId(object.getBucketName(), object.getKey()), object);
    return object;
  }

  /**
   * Finds an S3Object by bucket name and key.
   *
   * @param bucketName the name of the bucket containing the object
   * @param key the key identifying the object
   * @return the S3Object entity, or null if not found
   */
  public S3Object findByBucketAndKey(String bucketName, String key) {
    return OBJECTS.get(objectId(bucketName, key));
  }

  /**
   * Finds and lists all objects stored inside a specific bucket.
   *
   * @param bucketName the name of the bucket to query
   * @return a list of all S3Objects stored inside the bucket
   */
  public List<S3Object> findByBucket(String bucketName) {
    List<S3Object> objects = new ArrayList<>();
    for (S3Object object : OBJECTS.values()) {
      if (object.getBucketName().equals(bucketName)) {
        objects.add(object);
      }
    }
    return objects;
  }

  /**
   * Deletes a specific S3Object from the repository.
   *
   * @param bucketName the name of the bucket containing the object
   * @param key the key identifying the object to delete
   */
  public void delete(String bucketName, String key) {
    OBJECTS.remove(objectId(bucketName, key));
  }

  /**
   * Deletes all objects associated with a specific bucket name.
   * Typically called when a bucket is being deleted.
   *
   * @param bucketName the name of the bucket whose objects should be cleared
   */
  public void deleteByBucket(String bucketName) {
    List<String> keysToDelete = new ArrayList<>();
    for (Map.Entry<String, S3Object> entry : OBJECTS.entrySet()) {
      if (entry.getValue().getBucketName().equals(bucketName)) {
        keysToDelete.add(entry.getKey());
      }
    }
    for (String key : keysToDelete) {
      OBJECTS.remove(key);
    }
  }

  /**
   * Constructs a compounded lookup key from a bucket name and object key.
   *
   * @param bucketName the name of the bucket
   * @param key the object key
   * @return a compounded string key representing the unique identifier
   */
  private String objectId(String bucketName, String key) {
    return bucketName + "/" + key;
  }
}
