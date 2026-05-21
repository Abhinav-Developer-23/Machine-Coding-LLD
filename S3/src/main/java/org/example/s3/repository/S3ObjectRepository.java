package org.example.s3.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.example.s3.model.S3Object;

public class S3ObjectRepository {
  private static final S3ObjectRepository INSTANCE = new S3ObjectRepository();
  private static final ConcurrentHashMap<String, S3Object> OBJECTS = new ConcurrentHashMap<>();

  private S3ObjectRepository() {}

  public static S3ObjectRepository getInstance() {
    return INSTANCE;
  }

  public S3Object save(S3Object object) {
    OBJECTS.put(objectId(object.getBucketName(), object.getKey()), object);
    return object;
  }

  public S3Object findByBucketAndKey(String bucketName, String key) {
    return OBJECTS.get(objectId(bucketName, key));
  }

  public List<S3Object> findByBucket(String bucketName) {
    List<S3Object> objects = new ArrayList<>();
    for (S3Object object : OBJECTS.values()) {
      if (object.getBucketName().equals(bucketName)) {
        objects.add(object);
      }
    }
    return objects;
  }

  public void delete(String bucketName, String key) {
    OBJECTS.remove(objectId(bucketName, key));
  }

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

  private String objectId(String bucketName, String key) {
    return bucketName + "/" + key;
  }
}
