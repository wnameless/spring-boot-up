package com.github.wnameless.spring.boot.up.notification;

import java.time.Duration;
import com.github.oxo42.stateless4j.delegates.Action1;
import com.github.oxo42.stateless4j.delegates.Action2;
import com.github.oxo42.stateless4j.transitions.Transition;

public interface NotificationPlan<S, T> {

  S getState();

  T getTrigger();

  NotificationAdvice getAdvice();

  Action1<Transition<S, T>> getExitAction();

  Action2<Transition<S, T>, Object[]> getEntryAction();

  Runnable getAlwaysAction();

  Duration getAlwaysActionInterval();

}
