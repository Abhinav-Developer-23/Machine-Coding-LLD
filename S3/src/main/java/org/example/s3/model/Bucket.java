package org.example.s3.model;

import java.time.Instant;
import lombok.Getter;

/**
 * Represents an S3-like bucket containing objects/files. Tracks bucket name, owner identity,
 * creation timestamp, and bucket-level ACL settings.
 */
@Getter
public class Bucket {
  private final String name;
  private final String ownerUserId;
  private final Instant createdAt;
  private final Acl acl;

  /**
   * Constructs a new S3 Bucket with the specified name and owner.
   *
   * @param name the unique name of the bucket
   * @param ownerUserId the ID of the user creating and owning the bucket
   * @throws IllegalArgumentException if the name or ownerUserId is null or blank
   */
  public Bucket(String name, String ownerUserId) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Bucket name is required");
    }
    if (ownerUserId == null || ownerUserId.isBlank()) {
      throw new IllegalArgumentException("Bucket owner is required");
    }
    this.name = name;
    this.ownerUserId = ownerUserId;
    this.createdAt = Instant.now();
    this.acl = new Acl();
  }
}
