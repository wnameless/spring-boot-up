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

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import org.springframework.scheduling.annotation.Scheduled;
import net.sf.rubycollect4j.Ruby;
import net.sf.rubycollect4j.RubyArray;

public interface AsyncService<T> {

  CopyOnWriteArrayList<AsyncTask<T>> getAllAsyncTasks();

  default List<AsyncTaskState> getAllAsyncTaskStates() {
    return getAllAsyncTasks().stream().map(at -> new AsyncTaskState(at))
        .collect(Collectors.toList());
  }

  default void addAsyncTask(AsyncTask<T> task) {
    if (task.isMutuallyExclusive()) {
      for (AsyncTask<T> at : getAllAsyncTasks()) {
        if (Objects.equals(at.getName(), task.getName())
            && Objects.equals(at.getType(), task.getType())) {
          return;
        }
      }
    }

    task.execute();
    getAllAsyncTasks().add(task);
  }

  default void addAllAsyncTasks(List<AsyncTask<T>> tasks) {
    for (AsyncTask<T> at : tasks) {
      addAsyncTask(at);
    }
  }

  @Scheduled(fixedDelay = 1000)
  default void invokeActions() throws Exception {
    RubyArray<AsyncTask<T>> futures = Ruby.Array.of(getAllAsyncTasks());
    int beforeSize = futures.size();

    Integer index = futures.findIndex(e -> e.isDone());
    if (index != null) {
      AsyncTask<T> f = futures.deleteAt(index);
      if (f.getActionOnDone() != null) {
        f.getActionOnDone().accept(f.getCompletableFuture());
      }
    }

    index = futures.findIndex(e -> e.isCompletedExceptionally());
    if (index != null) {
      AsyncTask<T> f = futures.deleteAt(index);
      if (f.getActionOnCompletedExceptionally() != null) {
        f.getActionOnCompletedExceptionally().accept(f.getCompletableFuture());
      }
    }

    index = futures.findIndex(e -> e.isCancelled());
    if (index != null) {
      AsyncTask<T> f = futures.deleteAt(index);
      if (f.getActionOnCancelled() != null) {
        f.getActionOnCancelled().accept(f.getCompletableFuture());
      }
    }

    int afterSize = futures.size();
    if (beforeSize != afterSize && futures.isEmpty()) {
      afterEachTaskRound().run();
    }
  }

  default Runnable afterEachTaskRound() {
    return new Runnable() {

      @Override
      public void run() {}

    };
  }

}
