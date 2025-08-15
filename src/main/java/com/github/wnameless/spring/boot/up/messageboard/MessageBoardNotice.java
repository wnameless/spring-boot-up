package com.github.wnameless.spring.boot.up.messageboard;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import com.github.wnameless.spring.boot.up.model.TimeAuditable;

public interface MessageBoardNotice<MB extends MessageBoard> extends TimeAuditable {

  MB getMessageBoard();

  String getTitle();

  String getContent();

  Duration getTimelyDuration();

  boolean isPinned();

  default boolean isTimely() {
    if (getCreatedAt() == null) return false;

    if (getTimelyDuration() != null) {
      return LocalDateTime.now(Clock.systemUTC()).minusDays(getTimelyDuration().toDays())
          .isBefore(getCreatedAt());
    }

    var messageBoard = getMessageBoard();
    if (messageBoard != null && messageBoard.getTimelyDuration() != null) {
      return LocalDateTime.now(Clock.systemUTC())
          .minusDays(messageBoard.getTimelyDuration().toDays()).isBefore(getCreatedAt());
    }

    return false;
  }

}
