package com.github.wnameless.spring.boot.up.messageboard;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import com.github.wnameless.spring.boot.up.model.TimeAuditable;

public interface MessageBoardNotice<MB extends MessageBoard> extends TimeAuditable {

  MB getMessageBoard();

  String getTitle();

  String getMessageType();

  String getContent();

  Duration getTimelyDuration();

  boolean isPinned();

  default boolean isTimely() {
    if (getCreatedAt() == null) return false;

    if (getTimelyDuration() != null) {
      return LocalDateTime.ofInstant(Instant.now(), ZoneOffset.ofHours(0))
          .minusDays(getTimelyDuration().toDays()).toInstant(ZoneOffset.ofHours(0))
          .isBefore(getCreatedAt());
    }

    var messageBoard = getMessageBoard();
    if (messageBoard != null && messageBoard.getTimelyDuration() != null) {
      return LocalDateTime.ofInstant(Instant.now(), ZoneOffset.ofHours(0))
          .minusDays(getTimelyDuration().toDays()).toInstant(ZoneOffset.ofHours(0))
          .isBefore(getCreatedAt());
    }

    return false;
  }

}
