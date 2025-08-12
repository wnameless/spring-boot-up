package com.github.wnameless.spring.boot.up.notification;

import java.util.List;
import lombok.Data;

@Data
public final class BasicNotificationConfiguration implements NotificationConfiguration {

  String phaseTypeName;
  String ruleName;
  String onAdvice;
  Integer notificationInterval;
  String initTrigger;
  String targetState;
  String defaultProperties;
  String messageTitle;
  List<String> titleProperties;
  String messageContent;
  List<String> contentProperties;
  String actionPath;
  List<String> messageTo;
  String messageHook;
  String messageHookState;
  String messageHookTrigger;

}
