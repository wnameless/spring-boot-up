package com.github.wnameless.spring.boot.up.messageboard;

import java.time.Instant;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface MessageBoardNoticeRepository<N extends MessageBoardNotice<MB>, MB extends MessageBoard, ID>
    extends CrudRepository<N, ID> {

  List<N> findByMessageBoardAndCreatedAtAfter(MB messageBoard, Instant createdAfter);

  int countByMessageBoardAndCreatedAtAfter(MB messageBoard, Instant createdAfter);

  int countByMessageBoardAndTimelyDurationIsNotNullAndCreatedAtBetween(MB messageBoard,
      Instant createdAtStart, Instant createdAtEnd);

}
