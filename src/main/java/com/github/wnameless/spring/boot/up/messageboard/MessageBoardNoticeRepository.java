package com.github.wnameless.spring.boot.up.messageboard;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface MessageBoardNoticeRepository<N extends MessageBoardNotice<MB>, MB extends MessageBoard, ID>
    extends CrudRepository<N, ID> {

  List<N> findByMessageBoardAndCreatedAtAfter(MB messageBoard, LocalDateTime createdAfter);

  int countByMessageBoardAndCreatedAtAfter(MB messageBoard, LocalDateTime createdAfter);

}
