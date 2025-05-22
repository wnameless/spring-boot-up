package com.github.wnameless.spring.boot.up.autocreation;

import java.time.LocalDateTime;

public interface AutoCreationPlan<C> {

  String getAutoCreationPlanType();

  AutoCreationStrategy getAutoCreationStrategy();

  LocalDateTime getAutoCreationTimepoint();

  LocalDateTime getLastAutoCreationTimepoint();

  void setLastAutoCreationTimepoint(LocalDateTime lastTime);

  void execuateCreation();

  default boolean isExecutable() {
    return getAutoCreationStrategy().isNowExecutable(getLastAutoCreationTimepoint());
  }

}
