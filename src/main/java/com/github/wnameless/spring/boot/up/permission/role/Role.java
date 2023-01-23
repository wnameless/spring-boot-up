package com.github.wnameless.spring.boot.up.permission.role;

public interface Role {

  public static Role of(Role role) {
    return new SimpleRole(role);
  }

  public static Role of(String roleName) {
    return new SimpleRole(roleName);
  }

  String getRoleName();

  default Role toRole() {
    return Role.of(this);
  }

}
