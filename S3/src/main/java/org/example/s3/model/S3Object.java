package org.example.s3.model;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;

/**
 * Represents an S3 object (file) stored within a specific bucket.
 * Tracks location (bucket name), identification key, content, metadata tags, owners,
 * audit timestamps, and individual file-level ACL configurations.
 */
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

  /**
   * Constructs a new S3Object representing a file.
   *
   * @param bucketName the name of the containing bucket
   * @param key the unique key identifier for the file within the bucket
   * @param content the text content of the file
   * @param metadata key-value metadata tags
   * @param ownerUserId the ID of the user uploading and owning the file
   * @throws IllegalArgumentException if the bucketName, key, or ownerUserId is null or blank
   */
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

  /**
   * Updates the content and metadata of this object and updates the modified timestamp.
   *
   * @param content the new text content of the file
   * @param metadata the new key-value metadata tags
   */
  public void updateContent(String content, Map<String, String> metadata) {
    this.content = content == null ? "" : content;
    if (metadata != null) {
      this.metadata.clear();
      this.metadata.putAll(metadata);
    }
    this.updatedAt = Instant.now();
  }

  /**
   * Returns an unmodifiable snapshot of the object's metadata.
   *
   * @return an unmodifiable map containing the metadata tags
   */
  public Map<String, String> getMetadata() {
    return Collections.unmodifiableMap(metadata);
  }
}
