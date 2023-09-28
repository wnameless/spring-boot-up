package com.github.wnameless.spring.boot.up.notification;

import java.util.List;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.github.oxo42.stateless4j.StateRepresentation;

public interface NotificationStrategy<NT extends NotificationTarget<NS, NR, M, ID>, NS extends NotificationSource<ID>, SM extends NotifiableStateMachine<SM, S, T>, NR extends NotificationReceiver<M>, M, S, T, ID> {

  Class<SM> getNotifiableStateMachineType();

  NotificationService<NT, NS, NR, M, ID> getNotificationService();

  default void applyNotificationStrategy(StateMachineConfig<S, T> stateMachineConfig,
      SM stateMachine) {
    for (NotificationRule<S, T> rule : getNotificationRules(stateMachine)) {
      StateRepresentation<S, T> representation =
          stateMachineConfig.getRepresentation(rule.getState());

      switch (rule.getAdvice()) {
        case ENTRY:
          representation.addEntryAction(rule.getEntryAction());
          break;
        case ENTRY_FROM:
          representation.addEntryAction(rule.getTrigger(), rule.getEntryAction());
          break;
        case EXIT:
          representation.addExitAction(rule.getExitAction());
          break;
      }
    }
  }

  List<NotificationRule<S, T>> getNotificationRules(SM stateMachine);

  List<NotificationCallback<NS, SM, S, T, ID>> getNotificationCallbacks(SM stateMachine);

}
