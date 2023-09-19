package com.github.wnameless.spring.boot.up.permission.role;

public interface GlobalRoleEnum<E extends Enum<?> & Rolify> extends GlobalRole {

  E getRoleEnum();

  @Override
  default String getRoleName() {
    return getRoleEnum().getRoleName();
  }

}
