package org.example.s3.strategy;

import org.example.s3.enums.Permission;
import org.example.s3.model.Bucket;
import org.example.s3.model.S3Object;

public interface AuthorizationStrategy {
  boolean canAccessBucket(String userId, Bucket bucket, Permission permission);

  boolean canAccessObject(String userId, Bucket bucket, S3Object object, Permission permission);
}
