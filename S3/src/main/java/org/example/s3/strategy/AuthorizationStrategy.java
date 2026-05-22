package org.example.s3.strategy;

import org.example.s3.enums.Permission;
import org.example.s3.model.Bucket;
import org.example.s3.model.S3Object;

/**
 * Concrete strategy defining access rules and permission inheritance for S3 buckets and files.
 * Implements fallback and override logic where file-level ACL configurations override bucket-level ones.
 */
public class AuthorizationStrategy {

  /**
   * Evaluates if a user can access a bucket.
   * Access is allowed if the user is the bucket owner or has explicit bucket-level ACL permission.
   *
   * @param userId the ID of the user requesting access
   * @param bucket the target bucket
   * @param permission the requested permission (READ/WRITE)
   * @return true if access is authorized; false otherwise
   */
  public boolean canAccessBucket(String userId, Bucket bucket, Permission permission) {
    if (isBucketOwner(userId, bucket)) {
      return true;
    }
    return bucket.getAcl().allows(userId, permission);
  }

  /**
   * Evaluates if a user can access a specific file/object.
   * Access is allowed if:
   * 1. The user is the bucket owner or the file owner.
   * 2. The file has an explicit ACL entry for this user, in which case the file ACL determines access.
   * 3. The file has no explicit ACL entry for this user, in which case access falls back to bucket-level ACL.
   *
   * @param userId the ID of the user requesting access
   * @param bucket the bucket containing the file
   * @param object the target S3Object/file
   * @param permission the requested permission (READ/WRITE)
   * @return true if access is authorized; false otherwise
   */
  public boolean canAccessObject(
      String userId, Bucket bucket, S3Object object, Permission permission) {
    if (isBucketOwner(userId, bucket) || isObjectOwner(userId, object)) {
      return true;
    }

    if (object.getAcl().hasEntryFor(userId)) {
      return object.getAcl().allows(userId, permission);
    }

    return bucket.getAcl().allows(userId, permission);
  }

  /**
   * Internal helper to verify if the user is the owner of the bucket.
   */
  private boolean isBucketOwner(String userId, Bucket bucket) {
    return bucket.getOwnerUserId().equals(userId);
  }

  /**
   * Internal helper to verify if the user is the owner of the object.
   */
  private boolean isObjectOwner(String userId, S3Object object) {
    return object.getOwnerUserId().equals(userId);
  }
}
