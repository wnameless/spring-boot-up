package com.github.wnameless.spring.boot.up.notification;

import java.time.LocalDateTime;

public interface NotificationTarget<S extends NotificationSource<ID>, R extends NotificationReceiver<M>, M, ID> {

  ID getId();

  S getNotificationSource();

  R getNotificationReceiver();

  boolean isReviewed();

  void setReviewed(boolean reviewed);

  LocalDateTime getCreatedAt();

}
