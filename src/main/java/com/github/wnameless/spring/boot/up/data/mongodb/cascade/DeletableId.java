package com.github.wnameless.spring.boot.up.data.mongodb.cascade;

import lombok.Data;
import lombok.NonNull;

@Data(staticConstructor = "of")
public final class DeletableId {

  @NonNull
  private final Class<?> type;
  @NonNull
  private final Object id;

}
