package com.github.wnameless.spring.boot.up.web.function;

import java.util.Objects;

@FunctionalInterface
public interface TriConsumer<T, U, V> {

  void accept(T k, U v, V s);

  default TriConsumer<T, U, V> andThen(final TriConsumer<? super T, ? super U, ? super V> after) {
    Objects.requireNonNull(after);

    return (t, u, v) -> {
      accept(t, u, v);
      after.accept(t, u, v);
    };
  }

}
