package com.epam.finaltask.models;

import java.util.Set;

public enum Role {
  ADMIN(Set.of(
          Permission.ADMIN_READ,
          Permission.ADMIN_UPDATE,
          Permission.ADMIN_CREATE,
          Permission.ADMIN_DELETE,
          Permission.MANAGER_UPDATE,
          Permission.USER_READ,
          Permission.USER_UPDATE,
          Permission.USER_CREATE,
          Permission.USER_DELETE
  )),
  MANAGER(Set.of(
          Permission.MANAGER_UPDATE,
          Permission.USER_READ,
          Permission.USER_UPDATE
  )),
  USER(Set.of(
          Permission.USER_READ,
          Permission.USER_UPDATE
  ));

  private final Set<Permission> permissions;

  Role(Set<Permission> permissions) {
    this.permissions = permissions;
  }

  public Set<Permission> getPermissions() {
    return permissions;
  }
}
