package com.github.wnameless.spring.boot.up.fsm;

import lombok.Data;

@Data
public class ActiveTrigger<T extends Trigger> {

  private final T trigger;

  private String message;

  private boolean disable;

}
