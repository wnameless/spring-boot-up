package com.github.wnameless.spring.boot.up.notification;

import java.time.Duration;
import java.time.LocalDateTime;
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

  default NS createNotificationSource(String title, String content, String actionPath) {
    return createNotificationSource(title, content, actionPath, null);
  }

  NS createNotificationSource(String title, String content, String actionPath, ID senderId);

  NS findOrCreateNotificationSource(String title, String content, String actionPath, ID senderId);

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
        if (nr.getUpdatedAt().plus(alwaysActionInterval).isBefore(LocalDateTime.now())) {
          finalReceivers.add(reveiver);
        }
      } else {
        finalReceivers.add(reveiver);
      }
    }

    return finalReceivers;
  }

}
