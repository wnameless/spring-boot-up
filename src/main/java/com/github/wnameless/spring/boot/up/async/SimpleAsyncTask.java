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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.Data;

@Data
public class SimpleAsyncTask<T> implements AsyncTask<T> {

  public static <E> SimpleAsyncTask<E> of(String name, String type,
      Function<Consumer<String>, CompletableFuture<E>> completableFutureFunc) {
    return new SimpleAsyncTask<>(name, type,
        new CompletableFutureExecutor<>(completableFutureFunc));
  }

  private final String name;
  private final String type;
  private final CompletableFutureExecutor<T> completableFutureExecutor;
  private final long startTime = System.currentTimeMillis();

  private boolean mutuallyExclusive = true;
  private Map<String, ?> properties = new LinkedHashMap<>();
  private FutureConsumer<CompletableFuture<T>> actionOnDone = null;
  private FutureConsumer<CompletableFuture<T>> actionOnCompletedExceptionally =
      null;
  private FutureConsumer<CompletableFuture<T>> actionOnCancelled = null;

  public SimpleAsyncTask(String name, String type,
      CompletableFutureExecutor<T> completableFutureExecutor) {
    this.name = name;
    this.type = type;
    this.completableFutureExecutor = completableFutureExecutor;
  }

  @Override
  public String getStatus() {
    return completableFutureExecutor.getStatusGetter().get();
  }

}
