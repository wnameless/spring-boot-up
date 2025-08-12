package com.github.wnameless.spring.boot.up.notification;

import java.time.LocalDateTime;
import com.github.wnameless.spring.boot.up.web.IdProvider;

public interface NotificationSource<ID> extends IdProvider<ID> {

  String getTitle();

  String getContent();

  String getActionPath();

  LocalDateTime getCreatedAt();

  ID getSenderId();

}
