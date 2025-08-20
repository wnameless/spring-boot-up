package com.github.wnameless.spring.boot.up.actioncode;

import java.time.Instant;

public interface ActionCode<A extends Enum<?>, T> {

  T getActionTarget();

  void setActionTarget(T target);

  A getAction();

  void setAction(A action);

  String getCode();

  void setCode(String code);

  Instant getExpiredAt();

  void setExpiredAt(Instant expiredAt);

  default boolean isExpired() {
    if (getExpiredAt() == null) isValid();
    return Instant.now().isAfter(getExpiredAt());
  }

  default boolean isValid() {
    if (getExpiredAt() == null) return true;
    return Instant.now().isBefore(getExpiredAt());
  }

}
