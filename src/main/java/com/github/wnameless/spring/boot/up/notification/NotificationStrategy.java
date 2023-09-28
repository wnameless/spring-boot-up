package com.github.wnameless.spring.boot.up.notification;

import java.util.List;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.github.oxo42.stateless4j.StateRepresentation;
import com.github.oxo42.stateless4j.delegates.Action1;
import com.github.oxo42.stateless4j.delegates.Action2;
import com.github.oxo42.stateless4j.transitions.Transition;
import com.github.wnameless.spring.boot.up.fsm.Phase;
import com.github.wnameless.spring.boot.up.fsm.State;
import com.github.wnameless.spring.boot.up.fsm.Trigger;

public interface NotificationStrategy<NC extends NotificationCallback<NS, ID>, NT extends NotificationTarget<NS, NR, M, ID>, NS extends NotificationSource<ID>, SM extends NotifiableStateMachine<SM, S, T> & Phase<?, S, T, ID>, NR extends NotificationReceiver<M>, M, S extends State<T, ID>, T extends Trigger, ID> {

  Class<SM> getNotifiableStateMachineType();

  NotificationService<NC, NT, NS, NR, M, ID> getNotificationService();

  default void applyNotificationStrategy(StateMachineConfig<S, T> stateMachineConfig,
      SM stateMachine) {
    for (NotificationPlan<S, T> rule : getNotificationRules(stateMachine)) {
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

    for (NC callback : getNotificationCallbacks(stateMachine)) {
      @SuppressWarnings("unchecked")
      StateRepresentation<S, T> representation =
          stateMachineConfig.getRepresentation((S) callback.getState());

      switch (callback.getAdvice()) {
        case ENTRY:
          representation.addEntryAction(getNotificationCallbackAction2(callback));
          break;
        case ENTRY_FROM:
          @SuppressWarnings("unchecked")
          T trigger = (T) callback.getTrigger();
          representation.addEntryAction(trigger, getNotificationCallbackAction2(callback));
          break;
        case EXIT:
          representation.addExitAction(getNotificationCallbackAction1(callback));
          break;
      }
    }
  }

  List<NotificationPlan<S, T>> getNotificationRules(SM stateMachine);

  default List<NC> getNotificationCallbacks(SM stateMachine) {
    return getNotificationService().getNotificationCallbackRepository()
        .findAllByStateMachineEntityId(stateMachine.getEntity().getId());
  }

  default Action2<Transition<S, T>, Object[]> getNotificationCallbackAction2(NC callback) {
    return (arg1, arg2) -> {
      var targets = getNotificationService().getNotificationTargetRepository()
          .findAllByNotificationSource(callback.getNotificationSource());
      targets.forEach(t -> t.setReviewed(true));
      getNotificationService().getNotificationTargetRepository().saveAll(targets);
      getNotificationService().getNotificationCallbackRepository().delete(callback);
    };
  }

  default Action1<Transition<S, T>> getNotificationCallbackAction1(NC callback) {
    return (arg1) -> {
      var targets = getNotificationService().getNotificationTargetRepository()
          .findAllByNotificationSource(callback.getNotificationSource());
      targets.forEach(t -> t.setReviewed(true));
      getNotificationService().getNotificationTargetRepository().saveAll(targets);
      getNotificationService().getNotificationCallbackRepository().delete(callback);
    };
  }

}
