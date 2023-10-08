package com.github.wnameless.spring.boot.up.notification;

import com.github.oxo42.stateless4j.delegates.Action1;
import com.github.oxo42.stateless4j.delegates.Action2;
import com.github.oxo42.stateless4j.transitions.Transition;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationRule<S, T> implements NotificationPlan<S, T> {

  T trigger;

  S state;

  NotificationAdvice advice;

  Action1<Transition<S, T>> exitAction;

  Action2<Transition<S, T>, Object[]> entryAction;

  public NotificationRule() {}

  public NotificationRule(T trigger, S state, NotificationAdvice advice) {
    this.trigger = trigger;
    this.state = state;
    this.advice = advice;
  }

  public String getI18nMessage(Class<?> notificationStrategyType, String suffix) {
    return SpringBootUp.getMessage(notificationStrategyType.getSimpleName() + "." + trigger + "."
        + state + "." + advice + "." + suffix);
  }

}
