package com.github.wnameless.spring.boot.up.notification;

import java.time.LocalDateTime;
import com.github.wnameless.spring.boot.up.web.IdProvider;

public interface NotificationTarget<S extends NotificationSource<ID>, R extends NotificationReceiver<M>, M, ID>
    extends IdProvider<ID> {

  S getNotificationSource();

  R getNotificationReceiver();

  boolean isReviewed();

  void setReviewed(boolean reviewed);

  LocalDateTime getCreatedAt();

}
