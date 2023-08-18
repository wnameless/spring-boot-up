package com.github.wnameless.spring.boot.up.notification;

import java.time.LocalDateTime;

public interface NotificationSource<ID> {

  ID getId();

  String getTitle();

  String getContent();

  String getActionPath();

  LocalDateTime getCreatedAt();

}
