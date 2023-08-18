package com.github.wnameless.spring.boot.up.notification;

public interface NotificationReceiver<M> {

  String getUsername();

  M getUserMeta();

}
