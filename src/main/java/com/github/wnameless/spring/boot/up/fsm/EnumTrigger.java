package com.github.wnameless.spring.boot.up.fsm;

import com.github.wnameless.spring.boot.up.SpringBootUp;

public interface EnumTrigger extends Trigger {

  @Override
  default String getName() {
    return ((Enum<?>) this).name();
  }

  @Override
  default String getDisplayName() {
    return SpringBootUp.getMessage(this.getClass().getSimpleName() + "." + getName(), null,
        getName());
  }

}
