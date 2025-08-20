package com.github.wnameless.spring.boot.up.autocreation;

import java.time.Instant;

public interface AutoCreationPlan<C> {

  String getAutoCreationPlanType();

  AutoCreationStrategy getAutoCreationStrategy();

  Instant getAutoCreationTimepoint();

  Instant getLastAutoCreationTimepoint();

  void setLastAutoCreationTimepoint(Instant lastTime);

  void saveLastAutoCreationTimepoint(Instant lastTime);

  void execuateCreation();

  default boolean isExecutable() {
    return getAutoCreationStrategy().isNowExecutable(getLastAutoCreationTimepoint());
  }

}
