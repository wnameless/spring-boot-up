package com.github.wnameless.spring.boot.up.fsm;

import java.util.function.Function;
import com.github.oxo42.stateless4j.StateMachineConfig;

public interface StateMachineInitializable<S, T> {

  S getCurrentState();

  default StateMachineConfig<S, T> getStateMachineConfig() {
    if (stateMachineConfigStrategy() == null) {
      return getStateMachineConfigInternal();
    } else {
      return stateMachineConfigStrategy().apply(getStateMachineConfigInternal());
    }
  }

  StateMachineConfig<S, T> getStateMachineConfigInternal();

  default Function<StateMachineConfig<S, T>, StateMachineConfig<S, T>> stateMachineConfigStrategy() {
    return null;
  }

}
