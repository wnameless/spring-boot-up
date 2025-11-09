package com.github.wnameless.spring.boot.up.notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

  // Application-level cache to store callbacks indexed by state machine entity ID
  // This avoids race condition where callbacks are saved to DB but not yet visible when queried
  // Works across threads (unlike ThreadLocal) for background job scenarios
  // Key: state machine entity ID (as String)
  // Value: CacheEntry containing callbacks and timestamp for cleanup
  // Using raw types because generics cannot be used in static context
  Map<String, CacheEntry> CALLBACK_CACHE = new ConcurrentHashMap<>();

  // Cache entry with timestamp for automatic cleanup of old entries
  class CacheEntry {
    final List<?> callbacks;
    final long timestamp;

    CacheEntry(List<?> callbacks) {
      this.callbacks = new ArrayList<>(callbacks); // defensive copy
      this.timestamp = System.currentTimeMillis();
    }

    boolean isExpired(long maxAgeMillis) {
      return System.currentTimeMillis() - timestamp > maxAgeMillis;
    }
  }

  // Cache TTL: 5 minutes (callbacks should be used within this time)
  long CALLBACK_CACHE_TTL_MILLIS = 5 * 60 * 1000;

  // For backward compatibility, keep ThreadLocal as primary storage within same thread
  ThreadLocal<List<?>> COLLECTED_CALLBACKS = ThreadLocal.withInitial(ArrayList::new);

  List<T> getStateMachineTriggers();

  List<S> getStateMachineStates();

  List<CN> findConfigurableNotifications(SM stateMachine);

  NotificationPlan<S, T> convertToNotificationPlan(CN configurableNotification, SM stateMachine);

  @SuppressWarnings("unchecked")
  default List<NotificationPlan<S, T>> getNotificationPlans(SM stateMachine) {
    var notificationPlans = new ArrayList<NotificationPlan<S, T>>();

    // Clear any previously collected callbacks in ThreadLocal
    ((List<NC>) COLLECTED_CALLBACKS.get()).clear();

    // Cleanup expired cache entries (older than TTL)
    CALLBACK_CACHE.entrySet().removeIf(entry ->
        entry.getValue().isExpired(CALLBACK_CACHE_TTL_MILLIS));

    findConfigurableNotifications(stateMachine).forEach(cn -> {
      NotificationPlan<S, T> np = convertToNotificationPlan(cn, stateMachine);
      if (np != null) {
        notificationPlans.add(np);
      }
    });

    // Store collected callbacks in application-level cache indexed by state machine entity ID
    // This allows background threads to retrieve callbacks even if they weren't in the same thread
    var entityId = String.valueOf(stateMachine.getEntity().getId());
    var collectedCallbacks = (List<NC>) COLLECTED_CALLBACKS.get();
    if (!collectedCallbacks.isEmpty()) {
      CALLBACK_CACHE.put(entityId, new CacheEntry(collectedCallbacks));
    }

    return notificationPlans;
  }

}
