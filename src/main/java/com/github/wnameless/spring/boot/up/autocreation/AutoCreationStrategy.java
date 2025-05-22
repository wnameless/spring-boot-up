package com.github.wnameless.spring.boot.up.autocreation;

import java.time.LocalDateTime;
import org.springframework.lang.NonNull;

public enum AutoCreationStrategy {

  ONCE, HOURLY, DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY;

  public boolean isNowExecutable(LocalDateTime lastAutoCreationTimepoint) {
    return isExecutable(LocalDateTime.now(), lastAutoCreationTimepoint);
  }

  public boolean isExecutable(@NonNull LocalDateTime timeAt,
      LocalDateTime lastAutoCreationTimepoint) {
    switch (this) {
      case ONCE -> {
        return lastAutoCreationTimepoint == null;
      }
      default -> {
        if (lastAutoCreationTimepoint == null) return true;
        return getClosestTimepoint(lastAutoCreationTimepoint).isAfter(timeAt);
      }
    }
  }

  public LocalDateTime getClosestTimepoint(LocalDateTime baseDateTime) {
    LocalDateTime now = LocalDateTime.now();

    if (baseDateTime.isAfter(now)) return baseDateTime;

    switch (this) {
      case ONCE -> {
        return now;
      }
      case HOURLY -> {
        LocalDateTime result = baseDateTime;
        while (result.isBefore(now)) {
          result = result.plusHours(1);
        }
        return result;
      }
      case DAILY -> {
        LocalDateTime result = baseDateTime;
        while (result.isBefore(now)) {
          result = result.plusDays(1);
        }
        return result;
      }
      case WEEKLY -> {
        LocalDateTime result = baseDateTime;
        while (result.isBefore(now)) {
          result = result.plusWeeks(1);
        }
        return result;
      }
      case MONTHLY -> {
        LocalDateTime result = baseDateTime;
        while (result.isBefore(now)) {
          result = result.plusMonths(1);
        }
        return result;
      }
      case QUARTERLY -> {
        LocalDateTime result = baseDateTime;
        while (result.isBefore(now)) {
          result = result.plusMonths(3);
        }
        return result;
      }
      case YEARLY -> {
        LocalDateTime result = baseDateTime;
        while (result.isBefore(now)) {
          result = result.plusYears(1);
        }
        return result;
      }
      default -> {
        return baseDateTime;
      }
    }
  }

}
