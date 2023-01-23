package com.github.wnameless.spring.boot.up.permission.role;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
public class SimpleRole implements Role {

  @EqualsAndHashCode.Include
  private final String roleName;

  public SimpleRole(@NonNull Role role) {
    this.roleName = role.getRoleName().toUpperCase();
  }

  public SimpleRole(@NonNull String roleName) {
    this.roleName = roleName.toUpperCase();
  }

}
