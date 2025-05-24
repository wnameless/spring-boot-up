package com.github.wnameless.spring.boot.up.autocreation;

import java.time.LocalDateTime;
import com.github.wnameless.spring.boot.up.model.DataModelCRUDTrigger;
import com.github.wnameless.spring.boot.up.model.TimeAuditable;

public interface QuickAutoCreationPlan<T, C>
    extends DataModelCRUDTrigger<T>, AutoCreationPlan<C>, TimeAuditable {

  @Override
  default String getAutoCreationPlanType() {
    return getClass().getSimpleName();
  }

  @Override
  default LocalDateTime getAutoCreationTimepoint() {
    return getCreatedAt();
  }

  @Override
  default void saveLastAutoCreationTimepoint(LocalDateTime lastTime) {
    setLastAutoCreationTimepoint(lastTime);
    updateThisDataModel();
  }

}
