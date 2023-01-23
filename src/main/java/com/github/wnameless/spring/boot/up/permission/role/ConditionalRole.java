package com.github.wnameless.spring.boot.up.permission.role;

import java.util.function.BooleanSupplier;

public interface ConditionalRole extends WebRole {

  @Override
  default boolean isActive() {
    return getCondition().getAsBoolean();
  }

  BooleanSupplier getCondition();

}
