package com.github.wnameless.spring.boot.up.actioncode;

import java.time.LocalDateTime;

public interface SingularActionCode<A extends Enum<?>> {

  A getAction();

  void setAction(A action);

  String getCode();

  void setCode(String code);

  LocalDateTime getExpiredAt();

  void setExpiredAt(LocalDateTime expiredAt);

  default boolean isExpired() {
    if (getExpiredAt() == null) isValid();
    return LocalDateTime.now().isAfter(getExpiredAt());
  }

  default boolean isValid() {
    if (getExpiredAt() == null) return true;
    return LocalDateTime.now().isBefore(getExpiredAt());
  }

}
