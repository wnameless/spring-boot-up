package com.github.wnameless.spring.boot.up.fsm;

public interface Trigger {

  String getName();

  String getDisplayName();

  TriggerType getTriggerType();

}
