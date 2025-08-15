package com.github.wnameless.spring.boot.up.actioncode;

import java.time.Clock;
import java.time.LocalDateTime;

public interface ActionCode<A extends Enum<?>, T> {

  T getActionTarget();

  void setActionTarget(T target);

  A getAction();

  void setAction(A action);

  String getCode();

  void setCode(String code);

  LocalDateTime getExpiredAt();

  void setExpiredAt(LocalDateTime expiredAt);

  default boolean isExpired() {
    if (getExpiredAt() == null) isValid();
    return LocalDateTime.now(Clock.systemUTC()).isAfter(getExpiredAt());
  }

  default boolean isValid() {
    if (getExpiredAt() == null) return true;
    return LocalDateTime.now(Clock.systemUTC()).isBefore(getExpiredAt());
  }

}
