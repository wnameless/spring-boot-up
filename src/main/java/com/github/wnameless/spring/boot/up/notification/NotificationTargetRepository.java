package com.github.wnameless.spring.boot.up.notification;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface NotificationTargetRepository<NT extends NotificationTarget<S, R, M, ID>, S extends NotificationSource<ID>, R extends NotificationReceiver<M>, M, ID>
    extends CrudRepository<NT, ID> {

  List<NT> findAllByNotificationSource(S notificationSource);

}
