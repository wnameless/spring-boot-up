package com.github.wnameless.spring.boot.up.notification;

import com.github.wnameless.spring.boot.up.fsm.State;
import com.github.wnameless.spring.boot.up.fsm.Trigger;

public interface NotificationCallback<NS extends NotificationSource<ID>, ID> {

  ID getId();

  ID getStateMachineEntityId();

  NS getNotificationSource();

  NotificationAdvice getAdvice();

  State<?, ID> getState();

  Trigger getTrigger();

}
