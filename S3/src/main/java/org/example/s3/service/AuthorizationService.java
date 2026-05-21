package org.example.s3.service;

import org.example.s3.enums.Permission;
import org.example.s3.model.Bucket;
import org.example.s3.model.S3Object;
import org.example.s3.strategy.AuthorizationStrategy;
import org.example.s3.strategy.FileAclOverrideAuthorizationStrategy;

public class AuthorizationService {
  private final AuthorizationStrategy authorizationStrategy;

  public AuthorizationService() {
    this(new FileAclOverrideAuthorizationStrategy());
  }

  public AuthorizationService(AuthorizationStrategy authorizationStrategy) {
    this.authorizationStrategy = authorizationStrategy;
  }

  public boolean canAccessBucket(String userId, Bucket bucket, Permission permission) {
    return authorizationStrategy.canAccessBucket(userId, bucket, permission);
  }

  public boolean canAccessObject(
      String userId, Bucket bucket, S3Object object, Permission permission) {
    return authorizationStrategy.canAccessObject(userId, bucket, object, permission);
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
