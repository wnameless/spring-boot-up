package com.github.wnameless.spring.boot.up.notification;

import java.util.function.Function;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.fsm.StateMachineInitializable;

public interface NotifiableStateMachine<SM extends NotifiableStateMachine<SM, S, T>, S, T>
    extends StateMachineInitializable<S, T> {


  SM getNotifiableStateMachine();

  @SuppressWarnings("unchecked")
  @Override
  default Function<StateMachineConfig<S, T>, StateMachineConfig<S, T>> stateMachineConfigStrategy() {
    return c -> {
      var strategies = SpringBootUp.getBeansOfType(NotificationStrategy.class).values();
      for (var strategy : strategies) {
        if (strategy.getNotifiableStateMachineType().equals(this.getClass())) {
          strategy.applyNotificationStrategy(c, getNotifiableStateMachine());
        }
      }
      return c;
    };
  }

}
