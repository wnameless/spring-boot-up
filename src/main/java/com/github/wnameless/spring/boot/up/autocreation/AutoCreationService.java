package com.github.wnameless.spring.boot.up.autocreation;

import java.time.Clock;
import java.time.LocalDateTime;
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
    taskRegistrar.addCronTask(() -> {
      for (var autoCreator : getAutoCreators()) {
        for (var plan : autoCreator.getAutoCreationPlans()) {
          if (autoCreationPlanTypeStrategy().apply(plan.getAutoCreationPlanType())) {
            if (plan.isExecutable()) {
              plan.saveLastAutoCreationTimepoint(LocalDateTime.now(Clock.systemUTC()));
              plan.execuateCreation();
            }
          }
        }
      }
    }, getCronExpression());
  }

  default String getCronExpression() {
    return "0 */1 * * * *"; // Every 1 minutes
  }

}
