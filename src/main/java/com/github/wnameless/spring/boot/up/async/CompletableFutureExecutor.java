/*
 *
 * Copyright 2020 Wei-Ming Wu
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
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
