package com.github.wnameless.spring.boot.up.fsm;

import java.util.function.Function;
import com.github.oxo42.stateless4j.StateMachineConfig;

public interface StateMachineInitializable<S, T> {

  S getCurrentState();

  default StateMachineConfig<S, T> getStateMachineConfig() {
    if (stateMachineConfigStrategy() == null) {
      return getStateMachineConfigInternally();
    } else {
      return stateMachineConfigStrategy().apply(getStateMachineConfigInternally());
    }
  }

  StateMachineConfig<S, T> getStateMachineConfigInternally();

  default Function<StateMachineConfig<S, T>, StateMachineConfig<S, T>> stateMachineConfigStrategy() {
    return null;
  }

}
