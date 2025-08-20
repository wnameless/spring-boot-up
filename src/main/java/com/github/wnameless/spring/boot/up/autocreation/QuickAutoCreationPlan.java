package com.github.wnameless.spring.boot.up.autocreation;

import java.time.Instant;
import com.github.wnameless.spring.boot.up.model.DataModelCRUDTrigger;
import com.github.wnameless.spring.boot.up.model.TimeAuditable;

public interface QuickAutoCreationPlan<T, C>
    extends DataModelCRUDTrigger<T>, AutoCreationPlan<C>, TimeAuditable {

  @Override
  default String getAutoCreationPlanType() {
    return getClass().getSimpleName();
  }

  @Override
  default Instant getAutoCreationTimepoint() {
    return getCreatedAt();
  }

  @Override
  default void saveLastAutoCreationTimepoint(Instant lastTime) {
    setLastAutoCreationTimepoint(lastTime);
    updateThisDataModel();
  }

}
