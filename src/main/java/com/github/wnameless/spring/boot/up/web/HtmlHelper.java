package com.github.wnameless.spring.boot.up.web;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.attachment.AttachmentSnapshotProvider;
import com.github.wnameless.spring.boot.up.messageboard.MessageBoardService;

@Component("sbuHtml")
public class HtmlHelper {

  public static final String ITEM_FIELD_PREFIX = "sbu.web.item.field.";
  public static final String RESOURCE_PREFIX = "sbu.web.item.resource.";

  @Autowired
  ApplicationContext appCtx;
  @Autowired
  MessageSource messageSource;

  public MessageBoardService<?, ?, ?> getMessageBoardService() {
    return SpringBootUp.getBean(MessageBoardService.class);
  }

  public boolean isAttachmentSnapshotProvider(Object obj) {
    return obj instanceof AttachmentSnapshotProvider;
  }

  public String getSimpleName(Class<?> klass) {
    return (klass != null) ? klass.getSimpleName() : "";
  }

  public boolean hasBeanByType(Class<?> clazz) {
    return appCtx.getBeansOfType(clazz).size() > 0;
  }

  public String resourceDisplay(String className) {
    String[] nameParts = className.split(Pattern.quote("."));
    String lastPart = nameParts[nameParts.length - 1];
    return messageSource.getMessage( //
        ITEM_FIELD_PREFIX + lastPart, //
        null, lastPart, LocaleContextHolder.getLocale());
  }

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

  public String fieldDisplay(String typeSimpleName, String fieldName) {
    return messageSource.getMessage( //
        ITEM_FIELD_PREFIX + typeSimpleName + "." + fieldName, //
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

  public String toQueryString(Map<String, ?> params) {
    String queryString = params.entrySet().stream()
        .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "="
            + URLEncoder.encode(String.valueOf(e.getValue()), StandardCharsets.UTF_8))
        .collect(Collectors.joining("&"));
    return "&" + queryString;
  }

  public String fowardParam(Map<String, String[]> param,
      org.thymeleaf.context.WebEngineContext vars) {
    Map<String, Object> selectedMap = new LinkedHashMap<>();
    for (var key : param.keySet()) {
      if (key.startsWith(WebModelAttributes.FORWARDABLE_ATTRIBUTE_PREFIX)
          // Not only (_)
          && !WebModelAttributes.FORWARDABLE_ATTRIBUTE_PREFIX.equals(key)) {
        selectedMap.put(key, param.get(key));
      }
    }
    for (var name : vars.getVariableNames()) {
      if (name.startsWith(WebModelAttributes.FORWARDABLE_ATTRIBUTE_PREFIX)
          // Not only (_)
          && !WebModelAttributes.FORWARDABLE_ATTRIBUTE_PREFIX.equals(name)) {
        selectedMap.put(name, vars.getVariable(name));
      }
    }
    return toQueryString(selectedMap);
  };

  public String fowardParam(Map<String, String[]> param) {
    Map<String, Object> selectedMap = new LinkedHashMap<>();
    for (var key : param.keySet()) {
      if (key.startsWith(WebModelAttributes.FORWARDABLE_ATTRIBUTE_PREFIX)) {
        selectedMap.put(key, param.get(key));
      }
    }
    return toQueryString(selectedMap);
  };

  public int toYearsOld(int year) {
    int diff = LocalDate.now().getYear() - year;
    return diff < 0 ? 0 : diff;
  }

}
