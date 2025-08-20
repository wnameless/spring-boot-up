package com.github.wnameless.spring.boot.up.autocreation;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.lang.NonNull;

public enum AutoCreationStrategy {

  ONCE, MINUTELY, TEN_MINUTELY, FIFTEEN_MINUTELY, THIRTY_MINUTELY, HOURLY, DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY;

  public boolean isNowExecutable(Instant lastAutoCreationTimepoint) {
    return isExecutable(Instant.now(), lastAutoCreationTimepoint);
  }

  public boolean isExecutable(@NonNull Instant timeAt, Instant lastAutoCreationTimepoint) {
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

  public Instant getClosestTimepoint(Instant baseDateTime) {
    if (baseDateTime.isAfter(Instant.now())) return baseDateTime;

    var now = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.ofHours(0));
    switch (this) {
      case ONCE -> {
        return now.toInstant(ZoneOffset.ofHours(0));
      }
      case MINUTELY -> {
        LocalDateTime result = LocalDateTime.ofInstant(baseDateTime, ZoneOffset.ofHours(0));
        while (result.isBefore(now)) {
          result = result.plusMinutes(1);
        }
        return result.toInstant(ZoneOffset.ofHours(0));
      }
      case TEN_MINUTELY -> {
        LocalDateTime result = LocalDateTime.ofInstant(baseDateTime, ZoneOffset.ofHours(0));
        while (result.isBefore(now)) {
          result = result.plusMinutes(10);
        }
        return result.toInstant(ZoneOffset.ofHours(0));
      }
      case FIFTEEN_MINUTELY -> {
        LocalDateTime result = LocalDateTime.ofInstant(baseDateTime, ZoneOffset.ofHours(0));
        while (result.isBefore(now)) {
          result = result.plusMinutes(15);
        }
        return result.toInstant(ZoneOffset.ofHours(0));
      }
      case THIRTY_MINUTELY -> {
        LocalDateTime result = LocalDateTime.ofInstant(baseDateTime, ZoneOffset.ofHours(0));
        while (result.isBefore(now)) {
          result = result.plusMinutes(30);
        }
        return result.toInstant(ZoneOffset.ofHours(0));
      }
      case HOURLY -> {
        LocalDateTime result = LocalDateTime.ofInstant(baseDateTime, ZoneOffset.ofHours(0));
        while (result.isBefore(now)) {
          result = result.plusHours(1);
        }
        return result.toInstant(ZoneOffset.ofHours(0));
      }
      case DAILY -> {
        LocalDateTime result = LocalDateTime.ofInstant(baseDateTime, ZoneOffset.ofHours(0));
        while (result.isBefore(now)) {
          result = result.plusDays(1);
        }
        return result.toInstant(ZoneOffset.ofHours(0));
      }
      case WEEKLY -> {
        LocalDateTime result = LocalDateTime.ofInstant(baseDateTime, ZoneOffset.ofHours(0));
        while (result.isBefore(now)) {
          result = result.plusWeeks(1);
        }
        return result.toInstant(ZoneOffset.ofHours(0));
      }
      case MONTHLY -> {
        LocalDateTime result = LocalDateTime.ofInstant(baseDateTime, ZoneOffset.ofHours(0));
        while (result.isBefore(now)) {
          result = result.plusMonths(1);
        }
        return result.toInstant(ZoneOffset.ofHours(0));
      }
      case QUARTERLY -> {
        LocalDateTime result = LocalDateTime.ofInstant(baseDateTime, ZoneOffset.ofHours(0));
        while (result.isBefore(now)) {
          result = result.plusMonths(3);
        }
        return result.toInstant(ZoneOffset.ofHours(0));
      }
      case YEARLY -> {
        LocalDateTime result = LocalDateTime.ofInstant(baseDateTime, ZoneOffset.ofHours(0));
        while (result.isBefore(now)) {
          result = result.plusYears(1);
        }
        return result.toInstant(ZoneOffset.ofHours(0));
      }
      default -> {
        return baseDateTime;
      }
    }
  }

}
