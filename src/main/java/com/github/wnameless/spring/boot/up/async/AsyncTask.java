package com.github.wnameless.spring.boot.up.async;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface AsyncTask<T> {

  String getType();

  String getName();

  boolean isMutuallyExclusive();

  long getStartTime();

  String getStatus();

  Map<String, ?> getProperties();

  CompletableFutureExecutor<T> getCompletableFutureExecutor();

  default void execute() {
    if (!getCompletableFutureExecutor().isExecuted()) {
      getCompletableFutureExecutor().execute();
    }
  }

  default CompletableFuture<T> getCompletableFuture() {
    return getCompletableFutureExecutor().getCompletableFuture();
  }

  FutureConsumer<CompletableFuture<T>> getActionOnDone();

  default FutureConsumer<CompletableFuture<T>> getActionOnCompletedExceptionally() {
    return null;
  }

  default FutureConsumer<CompletableFuture<T>> getActionOnCancelled() {
    return null;
  }

  default boolean hasActionOnDone() {
    return getActionOnDone() != null;
  }

  default boolean hasActionOnCompletedExceptionally() {
    return getActionOnCompletedExceptionally() != null;
  }

  default boolean hasActionOnCancelled() {
    return getActionOnCancelled() != null;
  }

  default boolean isExcuted() {
    return getCompletableFutureExecutor().isExecuted();
  }

  default boolean isDone() {
    return isExcuted() && getCompletableFuture().isDone();
  }

  default boolean isCompletedExceptionally() {
    return isExcuted() && getCompletableFuture().isCompletedExceptionally();
  }

  default boolean isCancelled() {
    return isExcuted() && getCompletableFuture().isCancelled();
  }

}
