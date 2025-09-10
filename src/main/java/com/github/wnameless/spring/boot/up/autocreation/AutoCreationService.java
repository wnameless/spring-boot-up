package com.github.wnameless.spring.boot.up.autocreation;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.function.Function;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

public interface AutoCreationService extends SchedulingConfigurer {

  List<AutoCreator<?, ?>> getAutoCreators();

  Function<String, Boolean> autoCreationPlanTypeStrategy();

  default void configureTasks(@NonNull ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setScheduler(Executors.newSingleThreadScheduledExecutor());
    // Use fixed delay instead of cron - waits for completion before scheduling next execution
    taskRegistrar.addFixedDelayTask(() -> {
      for (var autoCreator : getAutoCreators()) {
        for (var plan : autoCreator.getAutoCreationPlans()) {
          if (autoCreationPlanTypeStrategy().apply(plan.getAutoCreationPlanType())) {
            if (plan.isExecutable()) {
              plan.saveLastAutoCreationTimepoint(Instant.now());
              plan.execuateCreation();
            }
          }
        }
      }
    }, getFixedDelay());
  }

  default Duration getFixedDelay() {
    return Duration.ofMinutes(1); // 1 minute delay after task completion
  }

}
