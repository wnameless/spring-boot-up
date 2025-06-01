package com.github.wnameless.spring.boot.up.messageboard;

import java.time.LocalDateTime;
import com.github.wnameless.spring.boot.up.model.TimeAuditable;

public interface MessageBoardNotice<MB extends MessageBoard> extends TimeAuditable {

  MB getMessageBoard();

  String getTitle();

  String getContent();

  boolean isPinned();

  default boolean isTimely() {
    var messageBoard = getMessageBoard();
    if (messageBoard == null || messageBoard.getTimelyDuration() == null
        || getCreatedAt() == null) {
      return false;
    }

    return LocalDateTime.now().minusDays(messageBoard.getTimelyDuration().toDays())
        .isBefore(getCreatedAt());
  }

}
