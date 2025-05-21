package com.github.wnameless.spring.boot.up.fsm;

import com.github.wnameless.spring.boot.up.SpringBootUp;

public interface EnumState<T extends Trigger, ID> extends State<T, ID> {

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
