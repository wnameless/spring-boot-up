package com.github.wnameless.spring.boot.up.messageboard;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.atteo.evo.inflector.English;
import org.springframework.core.ResolvableType;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.google.common.base.CaseFormat;
import net.sf.rubycollect4j.Ruby;

public interface MessageBoardService<N extends MessageBoardNotice<B>, B extends MessageBoard, ID> {

  @SuppressWarnings("unchecked")
  default MessageBoardRepository<B, ID> getMessageBoardRepository() {
    ResolvableType[] generics =
        ResolvableType.forClass(this.getClass()).as(MessageBoardService.class).getGenerics();
    return SpringBootUp.getBean(MessageBoardRepository.class, generics[1], generics[2]);
  }

  @SuppressWarnings("unchecked")
  default MessageBoardNoticeRepository<N, B, ID> getMessageBoardNoticeRepository() {
    ResolvableType[] generics =
        ResolvableType.forClass(this.getClass()).as(MessageBoardService.class).getGenerics();
    return SpringBootUp.getBean(MessageBoardNoticeRepository.class, generics[0], generics[1],
        generics[2]);
  }

  List<B> getMessageBoards();

  default List<N> getNoticesByBoardId(String boardId) {
    var messageBoardOpt = getMessageBoardRepository().findByBoardId(boardId);
    if (messageBoardOpt.isEmpty()) return Collections.emptyList();

    var messageBoard = messageBoardOpt.get();
    var now = LocalDateTime.now(Clock.systemUTC());
    var retentionDuration = messageBoard.getRetentionDuration();
    var notices = getMessageBoardNoticeRepository().findByMessageBoardAndCreatedAtAfter(
        messageBoard, now.minusDays(retentionDuration.toDays()));
    return Ruby.Array.of(notices).sortBy(n -> n.getCreatedAt()).reverse()
        .sortBy(n -> !n.isPinned());
  }

  default int getTimelyNoticeCountByBoardId(String boardId) {
    var messageBoardOpt = getMessageBoardRepository().findByBoardId(boardId);
    if (messageBoardOpt.isEmpty()) return 0;

    var messageBoard = messageBoardOpt.get();
    var now = LocalDateTime.now(Clock.systemUTC());
    var retentionDuration = messageBoard.getRetentionDuration();
    var timelyDuration = messageBoard.getTimelyDuration();
    return getMessageBoardNoticeRepository().countByMessageBoardAndCreatedAtAfter(messageBoard,
        now.minusDays(timelyDuration.toDays()))
        + getMessageBoardNoticeRepository()
            .countByMessageBoardAndTimelyDurationIsNotNullAndCreatedAtBetween(messageBoard,
                now.minusDays(retentionDuration.toDays()), now.minusDays(timelyDuration.toDays()));
  }

  String getDefaultBoardId();

  default String getMessageBoardPath(String boardId) {
    ResolvableType[] generics =
        ResolvableType.forClass(getClass()).as(MessageBoardService.class).getGenerics();
    var messageBoardType = generics[1];
    return "/" + English.plural(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN,
        messageBoardType.resolve().getSimpleName())) + "/board-ids/" + boardId + "/notices";
  }

}
