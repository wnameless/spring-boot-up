package com.github.wnameless.spring.boot.up.permission.resource;

import java.util.function.BooleanSupplier;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
public final class AccessControlRule {

  private final boolean overridable;
  @Accessors(fluent = true)
  private final BooleanSupplier accessControlRuleStock;

}
