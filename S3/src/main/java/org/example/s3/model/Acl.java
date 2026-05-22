package org.example.s3.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.example.s3.enums.Permission;

public class Acl {
  private final Map<String, Set<Permission>> userPermissions = new HashMap<>();

  public void grant(String userId, Permission permission) {
    validateUserId(userId);
    Set<Permission> permissions = userPermissions.get(userId);
    if (permissions == null) {
      permissions = new HashSet<>();
      userPermissions.putIfAbsent(userId, permissions);
    }
    permissions.add(permission);
  }

  public void revoke(String userId, Permission permission) {
    validateUserId(userId);
    Set<Permission> permissions = userPermissions.get(userId);
    if (permissions == null) {
      return;
    }
    permissions.remove(permission);
    if (permissions.isEmpty()) {
      userPermissions.remove(userId);
    }
  }

  public boolean hasEntryFor(String userId) {
    validateUserId(userId);
    return userPermissions.containsKey(userId);
  }

  public boolean allows(String userId, Permission permission) {
    validateUserId(userId);
    Set<Permission> permissions = userPermissions.get(userId);
    return permissions != null && permissions.contains(permission);
  }

  private void validateUserId(String userId) {
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("User id is required");
    }
  }
}
