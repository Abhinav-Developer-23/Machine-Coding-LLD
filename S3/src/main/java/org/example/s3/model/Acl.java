package org.example.s3.model;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.example.s3.enums.Permission;

public class Acl {
  private final Map<String, EnumSet<Permission>> userPermissions = new ConcurrentHashMap<>();

  public void grant(String userId, Permission permission) {
    validateUserId(userId);
    EnumSet<Permission> permissions = userPermissions.get(userId);
    if (permissions == null) {
      permissions = EnumSet.noneOf(Permission.class);
      userPermissions.put(userId, permissions);
    }
    permissions.add(permission);
  }

  public void revoke(String userId, Permission permission) {
    validateUserId(userId);
    EnumSet<Permission> permissions = userPermissions.get(userId);
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
    EnumSet<Permission> permissions = userPermissions.get(userId);
    return permissions != null && permissions.contains(permission);
  }

  public Map<String, EnumSet<Permission>> snapshot() {
    Map<String, EnumSet<Permission>> copy = new ConcurrentHashMap<>();
    for (Map.Entry<String, EnumSet<Permission>> entry : userPermissions.entrySet()) {
      copy.put(entry.getKey(), EnumSet.copyOf(entry.getValue()));
    }
    return Collections.unmodifiableMap(copy);
  }

  private void validateUserId(String userId) {
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("User id is required");
    }
  }
}
