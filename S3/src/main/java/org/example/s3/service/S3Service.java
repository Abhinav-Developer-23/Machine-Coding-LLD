package org.example.s3.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.example.s3.enums.Permission;
import org.example.s3.model.Bucket;
import org.example.s3.model.S3Object;
import org.example.s3.repository.BucketRepository;
import org.example.s3.repository.S3ObjectRepository;

public class S3Service {
  private final BucketRepository bucketRepository;
  private final S3ObjectRepository objectRepository;
  private final AuthorizationService authorizationService;

  public S3Service(
      BucketRepository bucketRepository,
      S3ObjectRepository objectRepository,
      AuthorizationService authorizationService) {
    this.bucketRepository = bucketRepository;
    this.objectRepository = objectRepository;
    this.authorizationService = authorizationService;
  }

  public Bucket createBucket(String userId, String bucketName) {
    return bucketRepository.save(new Bucket(bucketName, userId));
  }

  public List<String> listBuckets(String userId) {
    List<String> bucketNames = new ArrayList<>();
    for (Bucket bucket : bucketRepository.findAll()) {
      boolean canRead = authorizationService.canAccessBucket(userId, bucket, Permission.READ);
      boolean canWrite = authorizationService.canAccessBucket(userId, bucket, Permission.WRITE);
      if (canRead || canWrite) {
        bucketNames.add(bucket.getName());
      }
    }
    Collections.sort(bucketNames);
    return bucketNames;
  }

  public void deleteBucket(String userId, String bucketName) {
    Bucket bucket = getBucket(bucketName);
    authorizationService.requireBucketAccess(userId, bucket, Permission.WRITE);
    objectRepository.deleteByBucket(bucketName);
    bucketRepository.delete(bucketName);
  }

  public S3Object uploadFile(
      String userId, String bucketName, String key, String content, Map<String, String> metadata) {
    Bucket bucket = getBucket(bucketName);
    authorizationService.requireBucketAccess(userId, bucket, Permission.WRITE);
    return objectRepository.save(new S3Object(bucketName, key, content, metadata, userId));
  }

  public String readFile(String userId, String bucketName, String key) {
    Bucket bucket = getBucket(bucketName);
    S3Object object = getObject(bucketName, key);
    authorizationService.requireObjectAccess(userId, bucket, object, Permission.READ);
    return object.getContent();
  }

  public void updateFile(
      String userId, String bucketName, String key, String content, Map<String, String> metadata) {
    Bucket bucket = getBucket(bucketName);
    S3Object object = getObject(bucketName, key);
    authorizationService.requireObjectAccess(userId, bucket, object, Permission.WRITE);
    object.updateContent(content, metadata);
  }

  public void deleteFile(String userId, String bucketName, String key) {
    Bucket bucket = getBucket(bucketName);
    S3Object object = getObject(bucketName, key);
    authorizationService.requireObjectAccess(userId, bucket, object, Permission.WRITE);
    objectRepository.delete(bucketName, key);
  }

  public List<String> listFiles(String userId, String bucketName) {
    Bucket bucket = getBucket(bucketName);
    List<String> objectKeys = new ArrayList<>();
    for (S3Object object : objectRepository.findByBucket(bucketName)) {
      boolean canRead =
          authorizationService.canAccessObject(userId, bucket, object, Permission.READ);
      boolean canWrite =
          authorizationService.canAccessObject(userId, bucket, object, Permission.WRITE);
      if (canRead || canWrite) {
        objectKeys.add(object.getKey());
      }
    }
    Collections.sort(objectKeys);
    return objectKeys;
  }

  public void grantBucketPermission(
      String actorUserId, String bucketName, String targetUserId, Permission permission) {
    Bucket bucket = getBucket(bucketName);
    authorizationService.requireBucketOwner(actorUserId, bucket);
    bucket.getAcl().grant(targetUserId, permission);
  }

  public void revokeBucketPermission(
      String actorUserId, String bucketName, String targetUserId, Permission permission) {
    Bucket bucket = getBucket(bucketName);
    authorizationService.requireBucketOwner(actorUserId, bucket);
    bucket.getAcl().revoke(targetUserId, permission);
  }

  public void grantFilePermission(
      String actorUserId,
      String bucketName,
      String key,
      String targetUserId,
      Permission permission) {
    Bucket bucket = getBucket(bucketName);
    S3Object object = getObject(bucketName, key);
    authorizationService.requireObjectOwnerOrBucketOwner(actorUserId, bucket, object);
    object.getAcl().grant(targetUserId, permission);
  }

  public void revokeFilePermission(
      String actorUserId,
      String bucketName,
      String key,
      String targetUserId,
      Permission permission) {
    Bucket bucket = getBucket(bucketName);
    S3Object object = getObject(bucketName, key);
    authorizationService.requireObjectOwnerOrBucketOwner(actorUserId, bucket, object);
    object.getAcl().revoke(targetUserId, permission);
  }

  private Bucket getBucket(String bucketName) {
    Bucket bucket = bucketRepository.findByName(bucketName);
    if (bucket == null) {
      throw new IllegalArgumentException("Bucket not found: " + bucketName);
    }
    return bucket;
  }

  private S3Object getObject(String bucketName, String key) {
    S3Object object = objectRepository.findByBucketAndKey(bucketName, key);
    if (object == null) {
      throw new IllegalArgumentException("Object not found: " + key);
    }
    return object;
  }
}
