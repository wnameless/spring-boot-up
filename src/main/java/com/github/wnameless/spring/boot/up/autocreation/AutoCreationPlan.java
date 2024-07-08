package com.github.wnameless.spring.boot.up.autocreation;

import java.time.LocalDateTime;

public interface AutoCreationPlan<C> {

  AutoCreationStrategy getAutoCreationStrategy();

  LocalDateTime getAutoCreationTimepoint();

  void execuateCreation();

}
