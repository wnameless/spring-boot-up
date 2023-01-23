package com.github.wnameless.spring.boot.up.async;

@FunctionalInterface
public interface FutureConsumer<T> {

  void accept(T t) throws Exception;

}
