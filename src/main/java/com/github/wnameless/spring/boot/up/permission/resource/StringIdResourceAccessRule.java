package com.github.wnameless.spring.boot.up.permission.resource;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;

public interface StringIdResourceAccessRule<EP extends EntityPathBase<T>, RF extends ResourceFilterRepository<T, String>, T>
    extends QuickResourceAccessRule<EP, RF, T, String> {

  default Path<String> getIdFieldPath() {
    return Expressions.stringPath(q(), getIdFieldName());
  }

  default Expression<String> getIdValueExpression(String id) {
    return Expressions.constant(id);
  }

}
