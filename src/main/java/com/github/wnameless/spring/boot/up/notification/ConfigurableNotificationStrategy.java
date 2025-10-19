package com.github.wnameless.spring.boot.up.notification;

import java.util.ArrayList;
import java.util.List;
import com.github.wnameless.spring.boot.up.fsm.Phase;
import com.github.wnameless.spring.boot.up.fsm.State;
import com.github.wnameless.spring.boot.up.fsm.Trigger;

public interface ConfigurableNotificationStrategy< //
    CN, //
    NC extends NotificationCallback<NS, ID>, //
    NT extends NotificationTarget<NS, NR, M, ID>, //
    NS extends NotificationSource<ID>, //
    NR extends NotificationReceiver<M>, //
    M, //
    SM extends NotifiableStateMachine<SM, S, T> & Phase<?, S, T, ID>, //
    S extends State<T, ID>, //
    T extends Trigger, //
    ID> extends NotificationStrategy<NC, NT, NS, NR, M, SM, S, T, ID> {

  // ThreadLocal to collect callbacks created during getNotificationPlans()
  // This avoids race condition where callbacks are saved to DB but not yet visible when queried
  // Using raw List type because generics cannot be used in static context
  ThreadLocal<List<?>> COLLECTED_CALLBACKS = ThreadLocal.withInitial(ArrayList::new);

  List<T> getStateMachineTriggers();

  List<S> getStateMachineStates();

  List<CN> findConfigurableNotifications(SM stateMachine);

  NotificationPlan<S, T> convertToNotificationPlan(CN configurableNotification, SM stateMachine);

  @SuppressWarnings("unchecked")
  default List<NotificationPlan<S, T>> getNotificationPlans(SM stateMachine) {
    var notificationPlans = new ArrayList<NotificationPlan<S, T>>();

    // Clear any previously collected callbacks
    ((List<NC>) COLLECTED_CALLBACKS.get()).clear();

    findConfigurableNotifications(stateMachine).forEach(cn -> {
      NotificationPlan<S, T> np = convertToNotificationPlan(cn, stateMachine);
      if (np != null) {
        notificationPlans.add(np);
      }
    });

    return notificationPlans;
  }

}
