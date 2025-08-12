package com.github.wnameless.spring.boot.up.notification;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface NotificationSourceRepository<NS, ID> extends CrudRepository<NS, ID> {

  Optional<NS> findByTitleAndContentAndActionPathAndSenderId(String title, String content,
      String actionPath, ID senderId);

}
