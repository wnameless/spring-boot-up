package com.github.wnameless.spring.boot.up.web;

import com.querydsl.core.types.Path;

public interface QuerydslFilterRepository {

  Path<?>[] filterableProperties();

}
