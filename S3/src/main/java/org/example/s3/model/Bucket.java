package org.example.s3.model;

import java.time.Instant;
import lombok.Getter;

@Getter
public class Bucket {
  private final String name;
  private final String ownerUserId;
  private final Instant createdAt;
  private final Acl acl;

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
