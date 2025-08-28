package com.github.wnameless.spring.boot.up.notification;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public interface NotificationService<NC extends NotificationCallback<NS, ID>, NT extends NotificationTarget<NS, NR, M, ID>, NS extends NotificationSource<ID>, NR extends NotificationReceiver<M>, M, ID> {

  NotificationSourceRepository<NS, ID> getNotificationSourceRepository();

  NotificationTargetRepository<NT, NS, NR, M, ID> getNotificationTargetRepository();

  NotificationCallbackRepository<NC, NS, ID> getNotificationCallbackRepository();

  default boolean deleteNotificationCallback(NC callback) {
    return getNotificationCallbackRepository()
        .deleteByStateMachineEntityIdAndNotificationSourceAndAdviceAndTriggerNameAndTriggerEnumTypeNameAndStateNameAndStateEnumTypeName(
            callback.getStateMachineEntityId(), callback.getNotificationSource(),
            callback.getAdvice(), callback.getTriggerName(), callback.getTriggerEnumTypeName(),
            callback.getStateName(), callback.getStateEnumTypeName());
  }

  default boolean deleteNotificationCallback(ID stateMachineEntityId, NS notificationSource,
      NotificationAdvice advice, String triggerName, String triggerNameTypeName, String stateName,
      String stateEnumTypeName) {
    return getNotificationCallbackRepository()
        .deleteByStateMachineEntityIdAndNotificationSourceAndAdviceAndTriggerNameAndTriggerEnumTypeNameAndStateNameAndStateEnumTypeName(
            stateMachineEntityId, notificationSource, advice, triggerName, triggerNameTypeName,
            stateName, stateEnumTypeName);
  }

  default NC findOrCreateNotificationCallback(NC callback) {
    return findOrCreateNotificationCallback(callback.getStateMachineEntityId(),
        callback.getNotificationSource(), callback.getAdvice(), callback.getTriggerName(),
        callback.getTriggerEnumTypeName(), callback.getStateName(),
        callback.getStateEnumTypeName());
  }

  default NC findOrCreateNotificationCallback(ID stateMachineEntityId, NS notificationSource,
      NotificationAdvice advice, String triggerName, String triggerNameTypeName, String stateName,
      String stateEnumTypeName) {
    var notificationCallbackOpt = getNotificationCallbackRepository()
        .findByStateMachineEntityIdAndNotificationSourceAndAdviceAndTriggerNameAndTriggerEnumTypeNameAndStateNameAndStateEnumTypeName(
            stateMachineEntityId, notificationSource, advice, triggerName, triggerNameTypeName,
            stateName, stateEnumTypeName);
    if (notificationCallbackOpt.isPresent()) {
      return notificationCallbackOpt.get();
    } else {
      return createNotificationCallback(stateMachineEntityId, notificationSource, advice,
          triggerName, triggerNameTypeName, stateName, stateEnumTypeName);
    }
  }

  NC createNotificationCallback(ID stateMachineEntityId, NS notificationSource,
      NotificationAdvice advice, String triggerName, String triggerNameTypeName, String stateName,
      String stateEnumTypeName);

  default NS findOrCreateNotificationSource(String title, String content, String actionPath) {
    var notificationSourceOpt = getNotificationSourceRepository()
        .findByTitleAndContentAndActionPathAndSenderId(title, content, actionPath, null);
    if (notificationSourceOpt.isPresent()) {
      return notificationSourceOpt.get();
    }
    return createNotificationSource(title, content, actionPath, null);
  }

  NS createNotificationSource(String title, String content, String actionPath, ID senderId);

  default NS findOrCreateNotificationSource(String title, String content, String actionPath,
      ID senderId) {
    var notificationSourceOpt = getNotificationSourceRepository()
        .findByTitleAndContentAndActionPathAndSenderId(title, content, actionPath, senderId);
    if (notificationSourceOpt.isPresent()) {
      return notificationSourceOpt.get();
    }
    return createNotificationSource(title, content, actionPath, senderId);
  }

  default Collection<NT> deleteAllThenCreateNotificationTarget(NS source,
      Collection<NR> receivers) {
    List<NT> oldTargets = getNotificationTargetRepository().findAllByNotificationSource(source);
    getNotificationTargetRepository().deleteAll(oldTargets);

    return createNotificationTarget(source, receivers);
  }

  List<NT> createNotificationTarget(NS source, Collection<NR> receivers);

  default Collection<NR> getAlwaysTriggerNotificationReceivers(Duration alwaysActionInterval,
      NS source, Collection<NR> receivers) {
    List<NT> notificationTargets =
        getNotificationTargetRepository().findAllByNotificationSource(source).stream()
            .sorted(Comparator.comparing(NotificationTarget::getUpdatedAt))
            .collect(Collectors.toList());
    Collections.reverse(notificationTargets);

    if (notificationTargets.isEmpty()) {
      return receivers;
    }

    List<NR> finalReceivers = new ArrayList<>();
    for (NR reveiver : receivers) {
      var nrOpt = notificationTargets.stream()
          .filter(nt -> nt.getNotificationReceiver().equals(reveiver)).findFirst();
      if (nrOpt.isPresent()) {
        var nr = nrOpt.get();
        if (nr.getUpdatedAt().plus(alwaysActionInterval).isBefore(Instant.now())) {
          finalReceivers.add(reveiver);
        }
      } else {
        finalReceivers.add(reveiver);
      }
    }

    return finalReceivers;
  }

}
