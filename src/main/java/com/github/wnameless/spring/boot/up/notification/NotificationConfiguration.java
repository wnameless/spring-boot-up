package com.github.wnameless.spring.boot.up.notification;

import java.util.List;

public interface NotificationConfiguration {

  String getPhaseTypeName();

  String getRuleName();

  String getOnAdvice();

  String getInitTrigger();

  String getTargetState();

  String getDefaultProperties();

  String getMessageTitle();

  List<String> getTitleProperties();

  String getMessageContent();

  List<String> getContentProperties();

  String getActionPath();

  List<String> getMessageTo();

  String getMessageHook();

  String getMessageHookState();

  String getMessageHookTrigger();

}
