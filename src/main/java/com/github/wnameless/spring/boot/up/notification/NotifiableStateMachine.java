package com.github.wnameless.spring.boot.up.notification;

import java.util.function.Function;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.fsm.StateMachineInitializable;

public interface NotifiableStateMachine<SM extends NotifiableStateMachine<SM, S, T>, S, T>
    extends StateMachineInitializable<S, T> {

  @SuppressWarnings("unchecked")
  default SM getNotifiableStateMachine() {
    return (SM) this;
  }

  @SuppressWarnings("unchecked")
  @Override
  default Function<StateMachineConfig<S, T>, StateMachineConfig<S, T>> stateMachineConfigStrategy() {
    return c -> {
      var strategies = SpringBootUp.getBeansOfType(NotificationStrategy.class).values();
      for (var strategy : strategies) {
        var expectedClass = strategy.getNotifiableStateMachineType();
        var actualClass = this.getClass();

        // Check exact match OR if actual is subclass/proxy of expected
        // IMPORTANT: isAssignableFrom() is required to handle Spring CGLIB proxies
        // where actualClass might be "IrbApplicationPhase$$EnhancerBySpringCGLIB$$abc123"
        if (expectedClass.equals(actualClass) || expectedClass.isAssignableFrom(actualClass)) {
          strategy.applyNotificationStrategy(c, getNotifiableStateMachine());
        }
      }
      return c;
    };
  }

}
