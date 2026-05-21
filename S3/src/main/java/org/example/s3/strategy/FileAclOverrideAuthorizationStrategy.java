package org.example.s3.strategy;

import org.example.s3.enums.Permission;
import org.example.s3.model.Bucket;
import org.example.s3.model.S3Object;

public class FileAclOverrideAuthorizationStrategy implements AuthorizationStrategy {
  public boolean canAccessBucket(String userId, Bucket bucket, Permission permission) {
    if (isBucketOwner(userId, bucket)) {
      return true;
    }
    return bucket.getAcl().allows(userId, permission);
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

  private boolean isBucketOwner(String userId, Bucket bucket) {
    return bucket.getOwnerUserId().equals(userId);
  }

  private boolean isObjectOwner(String userId, S3Object object) {
    return object.getOwnerUserId().equals(userId);
  }
}
