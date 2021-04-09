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
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AsyncTasks {

  public <E> SimpleAsyncTask<E> of(String name, String type,
      Function<Consumer<String>, E> func) {
    Function<Consumer<String>, CompletableFuture<E>> completableFutureFunc;
    completableFutureFunc =
        (status) -> CompletableFuture.supplyAsync(() -> func.apply(status));

    return new SimpleAsyncTask<E>(name, type,
        new CompletableFutureExecutor<E>(completableFutureFunc));
  }

  public <E> SimpleAsyncTask<E> of(String name, String type,
      Function<Consumer<String>, E> func, Executor exec) {
    Function<Consumer<String>, CompletableFuture<E>> completableFutureFunc;
    completableFutureFunc = (status) -> CompletableFuture
        .supplyAsync(() -> func.apply(status), exec);

    return new SimpleAsyncTask<E>(name, type,
        new CompletableFutureExecutor<E>(completableFutureFunc));
  }

}
