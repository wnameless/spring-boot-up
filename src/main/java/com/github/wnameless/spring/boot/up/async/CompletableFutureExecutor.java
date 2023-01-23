package com.github.wnameless.spring.boot.up.async;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class CompletableFutureExecutor<T> {

  private final Function<Consumer<String>, CompletableFuture<T>> completableFutureFunc;
  private String status;
  private final Consumer<String> statusSetter = (s) -> status = s;
  private final Supplier<String> statusGetter = () -> status;

  private CompletableFuture<T> completableFuture;

  public CompletableFutureExecutor(
      Function<Consumer<String>, CompletableFuture<T>> completableFutureFunc) {
    this.completableFutureFunc = completableFutureFunc;
    statusSetter.accept("Processing");
  }

  public synchronized void execute() {
    if (completableFuture == null) {
      completableFuture = completableFutureFunc.apply(statusSetter);
    }
  }

  public CompletableFuture<T> getCompletableFuture() {
    return completableFuture;
  }

  public Consumer<String> getStatusSetter() {
    return statusSetter;
  }

  public Supplier<String> getStatusGetter() {
    return statusGetter;
  }

  public synchronized boolean isExecuted() {
    return completableFuture != null;
  }

}
