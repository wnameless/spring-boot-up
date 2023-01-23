package com.github.wnameless.spring.boot.up.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AsyncTasks {

  public <E> SimpleAsyncTask<E> of(String name, String type, Function<Consumer<String>, E> func) {
    Function<Consumer<String>, CompletableFuture<E>> completableFutureFunc;
    completableFutureFunc = (status) -> CompletableFuture.supplyAsync(() -> func.apply(status));

    return new SimpleAsyncTask<E>(name, type,
        new CompletableFutureExecutor<E>(completableFutureFunc));
  }

  public <E> SimpleAsyncTask<E> of(String name, String type, Function<Consumer<String>, E> func,
      Executor exec) {
    Function<Consumer<String>, CompletableFuture<E>> completableFutureFunc;
    completableFutureFunc =
        (status) -> CompletableFuture.supplyAsync(() -> func.apply(status), exec);

    return new SimpleAsyncTask<E>(name, type,
        new CompletableFutureExecutor<E>(completableFutureFunc));
  }

}
