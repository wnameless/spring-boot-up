package com.github.wnameless.spring.boot.up.web;

import org.springframework.data.repository.CrudRepository;

public interface RestfulRepositoryProvider<I, ID> {

  CrudRepository<I, ID> getRestfulRepository();

}
