package com.github.wnameless.spring.boot.up.actioncode;

import java.time.LocalDateTime;

public interface ActionCode<A extends Enum<?>> {

  A getAction();

  String getCode();

  LocalDateTime getExpiredAt();

  default boolean isExpired() {
    if (getExpiredAt() == null) isValid();
    return LocalDateTime.now().isAfter(getExpiredAt());
  }

  default boolean isValid() {
    if (getExpiredAt() == null) return true;
    return LocalDateTime.now().isBefore(getExpiredAt());
  }

}
