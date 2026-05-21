package org.example.s3.controller;

import java.util.Map;
import org.example.s3.enums.Permission;
import org.example.s3.repository.BucketRepository;
import org.example.s3.repository.S3ObjectRepository;
import org.example.s3.service.AuthorizationService;
import org.example.s3.service.S3Service;
import org.example.s3.strategy.AuthorizationStrategy;

public class S3Demo {
  public static void main(String[] args) {
    AuthorizationService authorizationService =
        new AuthorizationService(new AuthorizationStrategy());
    S3Service s3Service =
        new S3Service(
            BucketRepository.getInstance(), S3ObjectRepository.getInstance(), authorizationService);

    String alice = "alice";
    String bob = "bob";
    String charlie = "charlie";

    s3Service.createBucket(alice, "docs");
    s3Service.grantBucketPermission(alice, "docs", bob, Permission.READ);
    s3Service.grantBucketPermission(alice, "docs", bob, Permission.WRITE);

    s3Service.uploadFile(alice, "docs", "design.txt", "v1 design", Map.of("type", "text"));
    s3Service.uploadFile(bob, "docs", "notes.txt", "bob notes", Map.of("owner", bob));

    System.out.println("Alice buckets: " + s3Service.listBuckets(alice));
    System.out.println("Bob buckets: " + s3Service.listBuckets(bob));
    System.out.println("Bob files: " + s3Service.listFiles(bob, "docs"));
    System.out.println("Bob reads design: " + s3Service.readFile(bob, "docs", "design.txt"));

    s3Service.grantFilePermission(alice, "docs", "design.txt", bob, Permission.WRITE);
    s3Service.updateFile(bob, "docs", "design.txt", "v2 design", Map.of("type", "text"));
    System.out.println(
        "Alice reads updated design: " + s3Service.readFile(alice, "docs", "design.txt"));

    s3Service.grantFilePermission(alice, "docs", "design.txt", charlie, Permission.WRITE);
    s3Service.updateFile(charlie, "docs", "design.txt", "v3 design", Map.of("type", "text"));
    System.out.println(
        "Alice reads updated design (by Charlie): " + s3Service.readFile(alice, "docs", "design.txt"));

    s3Service.revokeFilePermission(alice, "docs", "design.txt", charlie, Permission.WRITE);
    try {
      s3Service.updateFile(charlie, "docs", "design.txt", "v4 design", Map.of("type", "text"));
    } catch (SecurityException ex) {
      System.out.println("Charlie update denied because WRITE permission on design.txt was revoked");
    }

    System.out.println(
        "Charlie files after file-level permission revoke: " + s3Service.listFiles(charlie, "docs"));
    try {
      s3Service.readFile(charlie, "docs", "design.txt");
    } catch (SecurityException ex) {
      System.out.println("Charlie read denied because file ACL overrides bucket ACL/default deny");
    }

    s3Service.deleteBucket(alice, "docs");
    System.out.println("Alice buckets after cascading delete: " + s3Service.listBuckets(alice));
  }
}
