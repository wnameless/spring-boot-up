package com.github.wnameless.spring.boot.up.autocreation;

import java.time.LocalDateTime;

public enum AutoCreationStrategy {

  HOURLY, DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY;

  public LocalDateTime getClosestTimepoint(LocalDateTime baseDateTime) {
    LocalDateTime now = LocalDateTime.now();

    if (baseDateTime.isAfter(now)) return baseDateTime;

    switch (this) {
      case HOURLY -> {
        LocalDateTime result = baseDateTime;
        while (result.isBefore(now)) {
          result = result.plusHours(1);
        }
        return result.minusHours(1);
      }
      case DAILY -> {
        LocalDateTime result = baseDateTime;
        while (result.isBefore(now)) {
          result = result.plusDays(1);
        }
        return result.minusDays(1);
      }
      case WEEKLY -> {
        LocalDateTime result = baseDateTime;
        while (result.isBefore(now)) {
          result = result.plusWeeks(1);
        }
        return result.minusWeeks(1);
      }
      case MONTHLY -> {
        LocalDateTime result = baseDateTime;
        while (result.isBefore(now)) {
          result = result.plusMonths(1);
        }
        return result.minusMonths(1);
      }
      case QUARTERLY -> {
        LocalDateTime result = baseDateTime;
        while (result.isBefore(now)) {
          result = result.plusMonths(3);
        }
        return result.minusMonths(3);
      }
      case YEARLY -> {
        LocalDateTime result = baseDateTime;
        while (result.isBefore(now)) {
          result = result.plusYears(1);
        }
        return result.minusYears(1);
      }
      default -> {
        return baseDateTime;
      }
    }
  }

}
