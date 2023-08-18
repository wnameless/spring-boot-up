package com.github.wnameless.spring.boot.up.notification;

import java.util.Collection;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface NotificationService<NT extends NotificationTarget<NS, NR, M, ID>, NS extends NotificationSource<ID>, NR extends NotificationReceiver<M>, M, ID> {

  CrudRepository<NS, ID> getNotificationSourceRepository();

  CrudRepository<NT, ID> getNotificationTargetRepository();

  NS createNotificationSource(String title, String content, String actionPath);

  List<NT> createNotificationTarget(NS source, Collection<NR> receivers);

}
