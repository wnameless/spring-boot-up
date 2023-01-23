package com.github.wnameless.spring.boot.up.permission.role;

import java.util.Optional;
import java.util.function.BooleanSupplier;

public interface InstanceRole<I> extends ConditionalRole {

  default BooleanSupplier getCondition() {
    return () -> getInstance().isPresent();
  }

  Optional<I> getInstance();

}
