package com.github.wnameless.spring.boot.up.notification;

public interface NotificationCallback<NS extends NotificationSource<ID>, SM extends NotifiableStateMachine<SM, S, T>, S, T, ID> {

  ID getId();

  ID getStateMachineId();

  NS getNotificationSource();

  NotificationAdvice getAdvice();

  S getState();

  T getTrigger();

}
