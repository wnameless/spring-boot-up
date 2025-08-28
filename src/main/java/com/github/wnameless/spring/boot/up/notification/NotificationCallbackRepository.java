package com.github.wnameless.spring.boot.up.notification;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface NotificationCallbackRepository<NC extends NotificationCallback<NS, ID>, NS extends NotificationSource<ID>, ID>
    extends CrudRepository<NC, ID> {

  List<NC> findAllByStateMachineEntityId(ID stateMachineEntityId);

  Optional<NC> findByStateMachineEntityIdAndNotificationSourceAndAdviceAndTriggerNameAndTriggerEnumTypeNameAndStateNameAndStateEnumTypeName(
      ID stateMachineEntityId, NS notificationSource, NotificationAdvice advice, String triggerName,
      String triggerNameTypeName, String stateName, String stateEnumTypeName);

  default boolean deleteByStateMachineEntityIdAndNotificationSourceAndAdviceAndTriggerNameAndTriggerEnumTypeNameAndStateNameAndStateEnumTypeName(
      ID stateMachineEntityId, NS notificationSource, NotificationAdvice advice, String triggerName,
      String triggerNameTypeName, String stateName, String stateEnumTypeName) {
    var opt =
        findByStateMachineEntityIdAndNotificationSourceAndAdviceAndTriggerNameAndTriggerEnumTypeNameAndStateNameAndStateEnumTypeName(
            stateMachineEntityId, notificationSource, advice, triggerName, triggerNameTypeName,
            stateName, stateEnumTypeName);
    if (opt.isPresent()) {
      deleteById(opt.get().getId());
      return true;
    }
    return false;
  }

}
