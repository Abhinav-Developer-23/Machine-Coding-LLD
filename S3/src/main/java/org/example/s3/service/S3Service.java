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

/**
 * Service orchestrating operations for the S3-like object storage system. Handles bucket
 * creation/deletion, file operations (upload, read, update, delete, list), and ACL management for
 * both buckets and individual files.
 */
public class S3Service {
  private final BucketRepository bucketRepository;
  private final S3ObjectRepository objectRepository;
  private final AuthorizationService authorizationService;

  /**
   * Constructs a new S3Service with required repositories and authorization service.
   *
   * @param bucketRepository the data repository for managing buckets
   * @param objectRepository the data repository for managing S3 objects/files
   * @param authorizationService the service responsible for validating request permissions
   */
  public S3Service(
      BucketRepository bucketRepository,
      S3ObjectRepository objectRepository,
      AuthorizationService authorizationService) {
    this.bucketRepository = bucketRepository;
    this.objectRepository = objectRepository;
    this.authorizationService = authorizationService;
  }

  /**
   * Creates a new S3 bucket with the specified owner.
   *
   * @param userId the ID of the user creating and owning the bucket
   * @param bucketName the unique name of the bucket to create
   * @return the newly created Bucket entity
   */
  public Bucket createBucket(String userId, String bucketName) {
    return bucketRepository.save(new Bucket(bucketName, userId));
  }

  /**
   * Lists the names of all buckets that the specified user has either READ or WRITE access to.
   *
   * @param userId the ID of the user requesting the list
   * @return a sorted list of accessible bucket names
   */
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

  /**
   * Deletes a bucket and cascades deletion to all objects contained within it. Requires the calling
   * user to have WRITE permission on the bucket.
   *
   * @param userId the ID of the user initiating deletion
   * @param bucketName the name of the bucket to delete
   * @throws SecurityException if the user does not have WRITE access to the bucket
   * @throws IllegalArgumentException if the bucket does not exist
   */
  public void deleteBucket(String userId, String bucketName) {
    Bucket bucket = getBucket(bucketName);
    authorizationService.requireBucketAccess(userId, bucket, Permission.WRITE);
    objectRepository.deleteByBucket(bucketName);
    bucketRepository.delete(bucketName);
  }

  /**
   * Uploads (creates) a new file within a bucket. Requires the calling user to have WRITE
   * permission on the destination bucket.
   *
   * @param userId the ID of the user uploading the file
   * @param bucketName the name of the destination bucket
   * @param key the unique key identifier for the file within the bucket
   * @param content the text content of the file
   * @param metadata key-value metadata tags for the file
   * @return the created S3Object entity
   * @throws SecurityException if the user does not have WRITE access to the bucket
   * @throws IllegalArgumentException if the bucket does not exist
   */
  public S3Object uploadFile(
      String userId, String bucketName, String key, String content, Map<String, String> metadata) {
    Bucket bucket = getBucket(bucketName);
    authorizationService.requireBucketAccess(userId, bucket, Permission.WRITE);
    return objectRepository.save(new S3Object(bucketName, key, content, metadata, userId));
  }

  /**
   * Reads and retrieves the content of a file. Requires the calling user to have READ access to the
   * file (either via file ACL, or inherited bucket ACL).
   *
   * @param userId the ID of the user reading the file
   * @param bucketName the name of the bucket containing the file
   * @param key the key of the file to read
   * @return the text content of the file
   * @throws SecurityException if the user is denied READ access
   * @throws IllegalArgumentException if the bucket or file does not exist
   */
  public String readFile(String userId, String bucketName, String key) {
    Bucket bucket = getBucket(bucketName);
    S3Object object = getObject(bucketName, key);
    authorizationService.requireObjectAccess(userId, bucket, object, Permission.READ);
    return object.getContent();
  }

  /**
   * Updates the content and metadata of an existing file. Requires the calling user to have WRITE
   * access to the file (either via file ACL, or inherited bucket ACL).
   *
   * @param userId the ID of the user updating the file
   * @param bucketName the name of the bucket containing the file
   * @param key the key of the file to update
   * @param content the new text content of the file
   * @param metadata the new key-value metadata tags
   * @throws SecurityException if the user is denied WRITE access
   * @throws IllegalArgumentException if the bucket or file does not exist
   */
  public void updateFile(
      String userId, String bucketName, String key, String content, Map<String, String> metadata) {
    Bucket bucket = getBucket(bucketName);
    S3Object object = getObject(bucketName, key);
    authorizationService.requireObjectAccess(userId, bucket, object, Permission.WRITE);
    object.updateContent(content, metadata);
  }

  /**
   * Deletes a specific file from a bucket. Requires the calling user to have WRITE access to the
   * file.
   *
   * @param userId the ID of the user deleting the file
   * @param bucketName the name of the bucket containing the file
   * @param key the key of the file to delete
   * @throws SecurityException if the user is denied WRITE access
   * @throws IllegalArgumentException if the bucket or file does not exist
   */
  public void deleteFile(String userId, String bucketName, String key) {
    Bucket bucket = getBucket(bucketName);
    S3Object object = getObject(bucketName, key);
    authorizationService.requireObjectAccess(userId, bucket, object, Permission.WRITE);
    objectRepository.delete(bucketName, key);
  }

  /**
   * Lists the keys of all files in a bucket that the user has READ or WRITE access to.
   *
   * @param userId the ID of the user requesting the list
   * @param bucketName the name of the bucket to query
   * @return a sorted list of keys for accessible files in the bucket
   * @throws IllegalArgumentException if the bucket does not exist
   */
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

  /**
   * Grants a bucket-level permission to a target user. Only the owner of the bucket can grant
   * bucket-level permissions.
   *
   * @param actorUserId the ID of the user executing the grant (must be the bucket owner)
   * @param bucketName the name of the bucket
   * @param targetUserId the ID of the user receiving the permission
   * @param permission the specific permission (READ/WRITE) to grant
   * @throws SecurityException if the actor is not the bucket owner
   * @throws IllegalArgumentException if the bucket does not exist
   */
  public void grantBucketPermission(
      String actorUserId, String bucketName, String targetUserId, Permission permission) {
    Bucket bucket = getBucket(bucketName);
    authorizationService.requireBucketOwner(actorUserId, bucket);
    bucket.getAcl().grant(targetUserId, permission);
  }

  /**
   * Revokes a bucket-level permission from a target user. Only the owner of the bucket can revoke
   * bucket-level permissions.
   *
   * @param actorUserId the ID of the user executing the revocation (must be the bucket owner)
   * @param bucketName the name of the bucket
   * @param targetUserId the ID of the user losing the permission
   * @param permission the specific permission (READ/WRITE) to revoke
   * @throws SecurityException if the actor is not the bucket owner
   * @throws IllegalArgumentException if the bucket does not exist
   */
  public void revokeBucketPermission(
      String actorUserId, String bucketName, String targetUserId, Permission permission) {
    Bucket bucket = getBucket(bucketName);
    authorizationService.requireBucketOwner(actorUserId, bucket);
    bucket.getAcl().revoke(targetUserId, permission);
  }

  /**
   * Grants a file-level permission to a target user. Requires the actor to be either the file owner
   * or the bucket owner.
   *
   * @param actorUserId the ID of the user executing the grant (must be file or bucket owner)
   * @param bucketName the name of the bucket containing the file
   * @param key the key of the file
   * @param targetUserId the ID of the user receiving the permission
   * @param permission the specific permission (READ/WRITE) to grant
   * @throws SecurityException if the actor is neither the file nor the bucket owner
   * @throws IllegalArgumentException if the bucket or file does not exist
   */
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

  /**
   * Revokes a file-level permission from a target user. Requires the actor to be either the file
   * owner or the bucket owner.
   *
   * @param actorUserId the ID of the user executing the revocation (must be file or bucket owner)
   * @param bucketName the name of the bucket containing the file
   * @param key the key of the file
   * @param targetUserId the ID of the user losing the permission
   * @param permission the specific permission (READ/WRITE) to revoke
   * @throws SecurityException if the actor is neither the file nor the bucket owner
   * @throws IllegalArgumentException if the bucket or file does not exist
   */
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

  /**
   * Internal helper method to look up a bucket by name.
   *
   * @param bucketName the name of the bucket to find
   * @return the Bucket entity
   * @throws IllegalArgumentException if the bucket is not found
   */
  private Bucket getBucket(String bucketName) {
    Bucket bucket = bucketRepository.findByName(bucketName);
    if (bucket == null) {
      throw new IllegalArgumentException("Bucket not found: " + bucketName);
    }
    return bucket;
  }

  /**
   * Internal helper method to look up a file by bucket and key.
   *
   * @param bucketName the name of the bucket containing the file
   * @param key the key of the file
   * @return the S3Object entity
   * @throws IllegalArgumentException if the file is not found
   */
  private S3Object getObject(String bucketName, String key) {
    S3Object object = objectRepository.findByBucketAndKey(bucketName, key);
    if (object == null) {
      throw new IllegalArgumentException("Object not found: " + key);
    }
    return object;
  }
}
