package com.github.wnameless.spring.boot.up.permission.role;

public interface InstanceRoleEnum<I, E extends Enum<?> & Rolify> extends InstanceRole<I> {

  E getRoleEnum();

  @Override
  default String getRoleName() {
    return getRoleEnum().getRoleName();
  }

}
