package com.github.wnameless.spring.boot.up.web;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component("sbuHtml")
public class HtmlHelper {

  public static final String ITEM_FIELD_PREFIX = "sbu.web.item.field.";

  @Autowired
  MessageSource messageSource;

  public String enumDisplay(Enum<?> enumVal) {
    return messageSource.getMessage( //
        ITEM_FIELD_PREFIX + enumVal.getDeclaringClass().getSimpleName() + "." + enumVal.name(), //
        null, enumVal.name(), LocaleContextHolder.getLocale());
  }

  public String fieldDisplay(Class<?> type, String fieldName) {
    return messageSource.getMessage( //
        ITEM_FIELD_PREFIX + type.getSimpleName() + "." + fieldName, //
        null, fieldName, LocaleContextHolder.getLocale());
  }

  public int randomInt() {
    return Math.abs(new Random().nextInt());
  }

  public String toTimeAgo(LocalDateTime dateTime) {
    LocalDateTime now = LocalDateTime.now();
    long diff = ChronoUnit.MINUTES.between(dateTime, now);

    if (diff < 1) {
      diff = ChronoUnit.SECONDS.between(dateTime, now);
      return messageSource.getMessage("sbu.web.time.ago.seconds", new Object[] {diff},
          diff == 1 ? "{0} second ago" : "{0} seconds ago", LocaleContextHolder.getLocale());
    } else if (diff >= 1 && diff < 60) {
      return messageSource.getMessage("sbu.web.time.ago.minutes", new Object[] {diff},
          diff == 1 ? "{0} minute ago" : "{0} minutes ago", LocaleContextHolder.getLocale());
    } else if (diff >= 60 && diff < 1440) {
      diff = ChronoUnit.HOURS.between(dateTime, now);
      return messageSource.getMessage("sbu.web.time.ago.hours", new Object[] {diff},
          diff == 1 ? "{0} hour ago" : "{0} hours ago", LocaleContextHolder.getLocale());
    }

    diff = ChronoUnit.DAYS.between(dateTime, now);
    return messageSource.getMessage("sbu.web.time.ago.days", new Object[] {diff},
        diff == 1 ? "{0} day ago" : "{0} days ago", LocaleContextHolder.getLocale());
  }

}
