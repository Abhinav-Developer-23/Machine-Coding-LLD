package org.example.s3.service;

import org.example.s3.enums.Permission;
import org.example.s3.model.Bucket;
import org.example.s3.model.S3Object;

public class AuthorizationService {
  public boolean canAccessBucket(String userId, Bucket bucket, Permission permission) {
    return isBucketOwner(userId, bucket) || bucket.getAcl().allows(userId, permission);
  }

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

  public void requireBucketOwner(String userId, Bucket bucket) {
    if (!isBucketOwner(userId, bucket)) {
      throw new SecurityException("Only bucket owner can perform this action");
    }
  }

  public void requireObjectOwnerOrBucketOwner(String userId, Bucket bucket, S3Object object) {
    if (!isBucketOwner(userId, bucket) && !isObjectOwner(userId, object)) {
      throw new SecurityException("Only bucket owner or object owner can perform this action");
    }
  }

  public void requireBucketAccess(String userId, Bucket bucket, Permission permission) {
    if (!canAccessBucket(userId, bucket, permission)) {
      throw new SecurityException("Access denied for bucket " + bucket.getName());
    }
  }

  public void requireObjectAccess(
      String userId, Bucket bucket, S3Object object, Permission permission) {
    if (!canAccessObject(userId, bucket, object, permission)) {
      throw new SecurityException("Access denied for object " + object.getKey());
    }
  }

  private boolean isBucketOwner(String userId, Bucket bucket) {
    return bucket.getOwnerUserId().equals(userId);
  }

  private boolean isObjectOwner(String userId, S3Object object) {
    return object.getOwnerUserId().equals(userId);
  }
}
