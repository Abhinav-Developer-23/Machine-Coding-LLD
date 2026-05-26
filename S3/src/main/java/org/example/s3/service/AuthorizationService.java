package org.example.s3.service;

import org.example.s3.enums.Permission;
import org.example.s3.model.Bucket;
import org.example.s3.model.S3Object;
import org.example.s3.strategy.AuthorizationStrategy;

/**
 * Service that handles checking and asserting permissions for bucket and object operations.
 * Delegates low-level checks to a configurable {@link AuthorizationStrategy}.
 */
public class AuthorizationService {
  private final AuthorizationStrategy authorizationStrategy;

  /** Constructs a new AuthorizationService using the default {@link AuthorizationStrategy}. */
  public AuthorizationService() {
    this(new AuthorizationStrategy());
  }

  /**
   * Constructs a new AuthorizationService with the designated strategy.
   *
   * @param authorizationStrategy the authorization strategy to delegate checks to
   */
  public AuthorizationService(AuthorizationStrategy authorizationStrategy) {
    this.authorizationStrategy = authorizationStrategy;
  }

  /**
   * Checks if the user is authorized to perform the given action on the bucket.
   *
   * @param userId the ID of the user requesting access
   * @param bucket the target bucket
   * @param permission the requested permission (READ/WRITE)
   * @return true if access is allowed; false otherwise
   */
  public boolean canAccessBucket(String userId, Bucket bucket, Permission permission) {
    return authorizationStrategy.canAccessBucket(userId, bucket, permission);
  }

  /**
   * Checks if the user is authorized to perform the given action on the specific file/object.
   *
   * @param userId the ID of the user requesting access
   * @param bucket the target bucket containing the file
   * @param object the target S3Object/file
   * @param permission the requested permission (READ/WRITE)
   * @return true if access is allowed; false otherwise
   */
  public boolean canAccessObject(
      String userId, Bucket bucket, S3Object object, Permission permission) {
    return authorizationStrategy.canAccessObject(userId, bucket, object, permission);
  }

  /**
   * Asserts that the user is the owner of the bucket.
   *
   * @param userId the ID of the user to assert ownership for
   * @param bucket the target bucket
   * @throws SecurityException if the user is not the bucket owner
   */
  public void requireBucketOwner(String userId, Bucket bucket) {
    if (!isBucketOwner(userId, bucket)) {
      throw new SecurityException("Only bucket owner can perform this action");
    }
  }

  /**
   * Asserts that the user is either the file/object owner or the bucket owner. Useful for
   * administrative and modification tasks (like file ACL management).
   *
   * @param userId the ID of the user to assert permissions for
   * @param bucket the containing bucket
   * @param object the target S3Object/file
   * @throws SecurityException if the user is neither the bucket owner nor the file owner
   */
  public void requireObjectOwnerOrBucketOwner(String userId, Bucket bucket, S3Object object) {
    if (!isBucketOwner(userId, bucket) && !isObjectOwner(userId, object)) {
      throw new SecurityException("Only bucket owner or object owner can perform this action");
    }
  }

  /**
   * Asserts that the user has the required permission (READ/WRITE) on the bucket.
   *
   * @param userId the ID of the user requesting access
   * @param bucket the target bucket
   * @param permission the required permission
   * @throws SecurityException if the user is denied access
   */
  public void requireBucketAccess(String userId, Bucket bucket, Permission permission) {
    if (!canAccessBucket(userId, bucket, permission)) {
      throw new SecurityException("Access denied for bucket " + bucket.getName());
    }
  }

  /**
   * Asserts that the user has the required permission (READ/WRITE) on the specific file/object.
   *
   * @param userId the ID of the user requesting access
   * @param bucket the target bucket containing the file
   * @param object the target S3Object/file
   * @param permission the required permission
   * @throws SecurityException if the user is denied access
   */
  public void requireObjectAccess(
      String userId, Bucket bucket, S3Object object, Permission permission) {
    if (!canAccessObject(userId, bucket, object, permission)) {
      throw new SecurityException("Access denied for object " + object.getKey());
    }
  }

  /** Internal helper to verify if the user owns the bucket. */
  private boolean isBucketOwner(String userId, Bucket bucket) {
    return bucket.getOwnerUserId().equals(userId);
  }

  /** Internal helper to verify if the user owns the specific object. */
  private boolean isObjectOwner(String userId, S3Object object) {
    return object.getOwnerUserId().equals(userId);
  }
}
