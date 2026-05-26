package org.example.s3.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.example.s3.enums.Permission;

/**
 * Models an Access Control List (ACL) for managing permissions. Maps individual user IDs to sets of
 * active {@link Permission}s.
 */
public class Acl {
  private final Map<String, Set<Permission>> userPermissions = new HashMap<>();

  /**
   * Grants a specific permission to the designated user. If no permissions exist for the user, a
   * new set is instantiated.
   *
   * @param userId the ID of the user receiving the permission
   * @param permission the permission (READ/WRITE) to grant
   * @throws IllegalArgumentException if the userId is null or blank
   */
  public void grant(String userId, Permission permission) {
    validateUserId(userId);
    Set<Permission> permissions = userPermissions.get(userId);
    if (permissions == null) {
      permissions = new HashSet<>();
      userPermissions.putIfAbsent(userId, permissions);
    }
    permissions.add(permission);
  }

  /**
   * Revokes a specific permission from the designated user. If the user's permission set becomes
   * empty, their entry is removed from the map.
   *
   * @param userId the ID of the user losing the permission
   * @param permission the permission (READ/WRITE) to revoke
   * @throws IllegalArgumentException if the userId is null or blank
   */
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

  /**
   * Checks if there is any explicit ACL entry defined for the specified user.
   *
   * @param userId the ID of the user to check
   * @return true if an entry exists for the user; false otherwise
   * @throws IllegalArgumentException if the userId is null or blank
   */
  public boolean hasEntryFor(String userId) {
    validateUserId(userId);
    return userPermissions.containsKey(userId);
  }

  /**
   * Verifies if the ACL explicitly allows the designated permission for the user.
   *
   * @param userId the ID of the user to check
   * @param permission the permission (READ/WRITE) to verify
   * @return true if the permission is explicitly allowed; false otherwise
   * @throws IllegalArgumentException if the userId is null or blank
   */
  public boolean allows(String userId, Permission permission) {
    validateUserId(userId);
    Set<Permission> permissions = userPermissions.get(userId);
    return permissions != null && permissions.contains(permission);
  }

  /**
   * Validates that the provided user ID is not null or blank.
   *
   * @param userId the user ID string to validate
   * @throws IllegalArgumentException if the user ID is invalid
   */
  private void validateUserId(String userId) {
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("User id is required");
    }
  }
}
