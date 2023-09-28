package com.github.wnameless.spring.boot.up.notification;

import java.util.Objects;
import lombok.SneakyThrows;

public interface NotificationCallback<NS extends NotificationSource<ID>, ID> {

  ID getId();

  ID getStateMachineEntityId();

  NS getNotificationSource();

  NotificationAdvice getAdvice();

  String getStateName();

  String getTriggerName();

  // Class<? extends Enum<? extends State<?, ID>>>
  String getStateEnumTypeName();

  // Class<? extends Enum<? extends Trigger>>
  String getTriggerEnumTypeName();

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
