package com.github.wnameless.spring.boot.up.fsm;

public interface StateMessageCondition<PP extends PhaseProvider<PP, S, T, ID>, S extends State<T, ID>, T extends Trigger, ID> {

  Boolean on(PP phaseProvider, S state);

  Boolean onEntry(PP phaseProvider, S state);

  Boolean onEntryFrom(PP phaseProvider, S state, T trigger);

  default String processMessage(String msgCode, PP phaseProvider, S state) {
    return null;
  }

}
