package com.github.wnameless.spring.boot.up.notification;

import java.util.ArrayList;
import java.util.List;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.github.oxo42.stateless4j.StateRepresentation;
import com.github.oxo42.stateless4j.delegates.Action1;
import com.github.oxo42.stateless4j.delegates.Action2;
import com.github.oxo42.stateless4j.transitions.Transition;
import com.github.wnameless.spring.boot.up.fsm.Phase;
import com.github.wnameless.spring.boot.up.fsm.State;
import com.github.wnameless.spring.boot.up.fsm.Trigger;

public interface NotificationStrategy<NC extends NotificationCallback<NS, ID>, //
    NT extends NotificationTarget<NS, NR, M, ID>, //
    NS extends NotificationSource<ID>, //
    NR extends NotificationReceiver<M>, //
    M, //
    SM extends NotifiableStateMachine<SM, S, T> & Phase<?, S, T, ID>, //
    S extends State<T, ID>, //
    T extends Trigger, //
    ID> {

  Class<SM> getNotifiableStateMachineType();

  NotificationService<NC, NT, NS, NR, M, ID> getNotificationService();

  default void applyAlwaysNotificationStrategy(SM stateMachine) {
    for (var np : getAlwaysNotificationPlans(stateMachine)) {
      np.getAlwaysAction().run();
    }
  }

  @SuppressWarnings("unchecked")
  default void applyNotificationStrategy(StateMachineConfig<S, T> stateMachineConfig,
      SM stateMachine) {
    for (NotificationPlan<S, T> rule : getNotificationPlans(stateMachine)) {
      StateRepresentation<S, T> representation =
          stateMachineConfig.getRepresentation(rule.getState());
      if (representation == null) {
        stateMachineConfig.configure(rule.getState());
        representation = stateMachineConfig.getRepresentation(rule.getState());
      }

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
        case ALWAYS:
          // ALWAYS advice is handled separately
          break;
      }
    }

    // Use collected callbacks from getNotificationPlans() to avoid race condition
    // where callbacks are saved to MongoDB but not yet visible when queried
    List<NC> callbacks = getCollectedCallbacks(stateMachine);
    for (NC callback : callbacks) {
      StateRepresentation<S, T> representation =
          stateMachineConfig.getRepresentation((S) callback.getState());
      if (representation == null) {
        stateMachineConfig.configure((S) callback.getState());
        representation = stateMachineConfig.getRepresentation((S) callback.getState());;
      }

      switch (callback.getAdvice()) {
        case ENTRY:
          representation.addEntryAction(getNotificationCallbackAction2(callback));
          break;
        case ENTRY_FROM:
          T trigger = (T) callback.getTrigger();
          representation.addEntryAction(trigger, getNotificationCallbackAction2(callback));
          break;
        case EXIT:
          representation.addExitAction(getNotificationCallbackAction1(callback));
          break;
        case ALWAYS:
          // ALWAYS advice has no callback
          break;
      }
    }
  }

  List<NotificationPlan<S, T>> getNotificationPlans(SM stateMachine);

  default List<NotificationPlan<S, T>> getAlwaysNotificationPlans(SM stateMachine) {
    return getNotificationPlans(stateMachine).stream()
        .filter(np -> np.getAdvice() == NotificationAdvice.ALWAYS).toList();
  }

  default List<NC> getNotificationCallbacks(SM stateMachine) {
    return getNotificationService().getNotificationCallbackRepository()
        .findAllByStateMachineEntityId(stateMachine.getEntity().getId());
  }

  @SuppressWarnings("unchecked")
  default List<NC> getCollectedCallbacks(SM stateMachine) {
    // If this is a ConfigurableNotificationStrategy, use collected callbacks to avoid race
    // condition
    if (this instanceof ConfigurableNotificationStrategy) {
      try {
        var collectedCallbacks =
            (List<NC>) ConfigurableNotificationStrategy.COLLECTED_CALLBACKS.get();
        // Return copy and clear ThreadLocal to prevent memory leaks
        List<NC> result = new ArrayList<>(collectedCallbacks);
        collectedCallbacks.clear();
        ConfigurableNotificationStrategy.COLLECTED_CALLBACKS.remove();
        return result;
      } catch (Exception e) {
        // Fall back to database query if there's any issue
      }
    }
    // Fall back to querying database (for non-configurable strategies or if collection failed)
    return getNotificationCallbacks(stateMachine);
  }

  default Action2<Transition<S, T>, Object[]> getNotificationCallbackAction2(NC callback) {
    return (arg1, arg2) -> {
      var targets = getNotificationService().getNotificationTargetRepository()
          .findAllByNotificationSource(callback.getNotificationSource());
      targets.forEach(t -> t.setReviewed(true));
      getNotificationService().getNotificationTargetRepository().saveAll(targets);
      getNotificationService().deleteNotificationCallback(callback);
    };
  }

  default Action1<Transition<S, T>> getNotificationCallbackAction1(NC callback) {
    return (arg1) -> {
      var targets = getNotificationService().getNotificationTargetRepository()
          .findAllByNotificationSource(callback.getNotificationSource());
      targets.forEach(t -> t.setReviewed(true));
      getNotificationService().getNotificationTargetRepository().saveAll(targets);
      getNotificationService().deleteNotificationCallback(callback);
    };
  }

}
