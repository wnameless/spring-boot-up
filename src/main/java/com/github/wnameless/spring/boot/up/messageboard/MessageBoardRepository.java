package com.github.wnameless.spring.boot.up.messageboard;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface MessageBoardRepository<MB, ID> extends CrudRepository<MB, ID> {

  Optional<MB> findByBoardId(String boardId);

}
