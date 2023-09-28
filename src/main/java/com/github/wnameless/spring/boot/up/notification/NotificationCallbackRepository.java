package com.github.wnameless.spring.boot.up.notification;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface NotificationCallbackRepository<NC extends NotificationCallback<NS, ID>, NS extends NotificationSource<ID>, ID>
    extends CrudRepository<NC, ID> {

  List<NC> findAllByStateMachineEntityId(ID stateMachineEntityId);

}
