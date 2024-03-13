package com.github.wnameless.spring.boot.up.notification;

import java.util.Objects;
import com.github.wnameless.spring.boot.up.fsm.State;
import com.github.wnameless.spring.boot.up.fsm.Trigger;
import com.github.wnameless.spring.boot.up.web.IdProvider;
import lombok.SneakyThrows;

public interface NotificationCallback<NS extends NotificationSource<ID>, ID>
    extends IdProvider<ID> {

  ID getStateMachineEntityId();

  void setStateMachineEntityId(ID id);

  NS getNotificationSource();

  void setNotificationSource(NS notificationSource);

  NotificationAdvice getAdvice();

  void setAdvice(NotificationAdvice advice);

  default void setState(Enum<? extends State<?, ID>> state) {
    setStateName(state.name());
    setStateEnumTypeName(state.getDeclaringClass().getName());
  }

  String getStateName();

  void setStateName(String stateName);

  default void setTrigger(Enum<? extends Trigger> trigger) {
    setTriggerName(trigger.name());
    setTriggerEnumTypeName(trigger.getDeclaringClass().getName());
  }

  String getTriggerName();

  void setTriggerName(String triggerName);

  // Class<? extends Enum<? extends State<?, ID>>>
  String getStateEnumTypeName();

  void setStateEnumTypeName(String stateEnumTypeName);

  // Class<? extends Enum<? extends Trigger>>
  String getTriggerEnumTypeName();

  void setTriggerEnumTypeName(String triggerEnumTypeName);

  @SuppressWarnings("unchecked")
  @SneakyThrows
  default Enum<?> getState() {
    Class<? extends Enum<?>> enumType =
        (Class<? extends Enum<?>>) Class.forName(getStateEnumTypeName());
    for (Enum<?> e : enumType.getEnumConstants()) {
      if (Objects.equals(e.name(), getStateName())) {
        return e;
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  @SneakyThrows
  default Enum<?> getTrigger() {
    Class<? extends Enum<?>> enumType =
        (Class<? extends Enum<?>>) Class.forName(getTriggerEnumTypeName());
    for (Enum<?> e : enumType.getEnumConstants()) {
      if (Objects.equals(e.name(), getTriggerName())) {
        return e;
      }
    }
    return null;
  }

}
