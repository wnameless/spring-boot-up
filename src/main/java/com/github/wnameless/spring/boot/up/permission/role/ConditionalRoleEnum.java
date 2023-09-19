package com.github.wnameless.spring.boot.up.permission.role;

import java.util.function.BooleanSupplier;

public interface ConditionalRoleEnum<E extends Enum<?> & Rolify> extends ConditionalRole {

  E getRoleEnum();

  @Override
  default String getRoleName() {
    return getRoleEnum().getRoleName();
  }

  @Override
  default boolean isActive() {
    return getCondition().getAsBoolean();
  }

  BooleanSupplier getCondition();

}
