package org.example.s3.model;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;

@Getter
public class S3Object {
  private final String bucketName;
  private final String key;
  private final String ownerUserId;
  private final Instant createdAt;
  private final Acl acl;
  private String content;
  private Instant updatedAt;
  private final Map<String, String> metadata;

  public S3Object(
      String bucketName,
      String key,
      String content,
      Map<String, String> metadata,
      String ownerUserId) {
    if (bucketName == null || bucketName.isBlank()) {
      throw new IllegalArgumentException("Bucket name is required");
    }
    if (key == null || key.isBlank()) {
      throw new IllegalArgumentException("Object key is required");
    }
    if (ownerUserId == null || ownerUserId.isBlank()) {
      throw new IllegalArgumentException("Object owner is required");
    }
    this.bucketName = bucketName;
    this.key = key;
    this.ownerUserId = ownerUserId;
    this.content = content == null ? "" : content;
    this.metadata = new ConcurrentHashMap<>(metadata == null ? Map.of() : metadata);
    this.createdAt = Instant.now();
    this.updatedAt = createdAt;
    this.acl = new Acl();
  }

  public void updateContent(String content, Map<String, String> metadata) {
    this.content = content == null ? "" : content;
    if (metadata != null) {
      this.metadata.clear();
      this.metadata.putAll(metadata);
    }
    this.updatedAt = Instant.now();
  }

  public Map<String, String> getMetadata() {
    return Collections.unmodifiableMap(metadata);
  }
}
