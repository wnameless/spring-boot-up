package com.github.wnameless.spring.boot.up.async;

import java.util.Collections;
import java.util.Map;
import lombok.Data;

@Data
public final class AsyncTaskState {

  private final String name;
  private final String type;
  private final long executionTime;
  private final String status;
  private final Map<String, ?> properties;

  public AsyncTaskState(AsyncTask<?> task) {
    name = task.getName();
    type = task.getType();
    executionTime = (System.currentTimeMillis() - task.getStartTime()) / 1000;
    status = task.getStatus();
    properties = Collections.unmodifiableMap(task.getProperties());
  }

}
