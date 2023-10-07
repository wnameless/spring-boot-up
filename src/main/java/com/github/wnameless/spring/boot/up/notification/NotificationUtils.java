package com.github.wnameless.spring.boot.up.notification;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import com.github.wnameless.spring.boot.up.SpringBootUp;

public final class NotificationUtils {

  private NotificationUtils() {}

  public static String toTimeAgo(LocalDateTime dateTime) {
    LocalDateTime now = LocalDateTime.now();
    long diff = ChronoUnit.MINUTES.between(dateTime, now);

    if (diff < 1) {
      diff = ChronoUnit.SECONDS.between(dateTime, now);
      return SpringBootUp.getMessage("sbu.notification.time.ago.seconds", diff,
          diff == 1 ? "{0} second ago" : "{0} seconds ago");
    } else if (diff >= 1 && diff < 60) {
      return SpringBootUp.getMessage("sbu.notification.time.ago.minutes", diff,
          diff == 1 ? "{0} minute ago" : "{0} minutes ago");
    } else if (diff >= 60 && diff < 1440) {
      diff = ChronoUnit.HOURS.between(dateTime, now);
      return SpringBootUp.getMessage("sbu.notification.time.ago.hours", diff,
          diff == 1 ? "{0} hour ago" : "{0} hours ago");
    }

    diff = ChronoUnit.DAYS.between(dateTime, now);
    return SpringBootUp.getMessage("sbu.notification.time.ago.days", diff,
        diff == 1 ? "{0} day ago" : "{0} days ago");
  }

}
