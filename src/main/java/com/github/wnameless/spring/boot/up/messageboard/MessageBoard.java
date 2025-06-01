package com.github.wnameless.spring.boot.up.messageboard;

import java.time.Duration;

public interface MessageBoard {

  String getBoardId();

  String getBoardName();

  Duration getRetentionDuration();

  Duration getTimelyDuration();

}
