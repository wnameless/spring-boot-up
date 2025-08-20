package com.github.wnameless.spring.boot.up.autocreation;

import java.time.Instant;
import java.util.List;

public interface AutoCreator<T extends AutoCreationPlan<C>, C> {

  List<T> getAutoCreationPlans();

  default void execuateAutoCreationPlans() {
    getAutoCreationPlans().forEach(p -> {
      p.setLastAutoCreationTimepoint(Instant.now());
      p.execuateCreation();
    });
  }

}
