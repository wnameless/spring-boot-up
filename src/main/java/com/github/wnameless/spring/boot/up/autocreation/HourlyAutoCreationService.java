package com.github.wnameless.spring.boot.up.autocreation;

import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;

public interface HourlyAutoCreationService {

  List<AutoCreator<?, ?>> getAutoCreators();

  @Scheduled(cron = "0 0 * * * ?")
  default void execuateAutoCreationPlans() {
    for (var autoCreator : getAutoCreators()) {
      autoCreator.execuateAutoCreationPlans();
    }
  }

}
