package com.github.wnameless.spring.boot.up.permission.role;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
public final class Role implements Rolify {

  public static Role of(Rolify role) {
    return new Role(role);
  }

  public static Role of(String roleName) {
    return new Role(roleName);
  }

  @EqualsAndHashCode.Include
  private String roleName;

  public Role() {}

  public Role(@NonNull Rolify role) {
    this.roleName = role.getRoleName().toUpperCase();
  }

  public Role(@NonNull String roleName) {
    this.roleName = roleName.toUpperCase();
  }

}
