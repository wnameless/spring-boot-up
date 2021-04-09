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
package com.github.wnameless.spring.boot.up.permission.resource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.springframework.data.annotation.Id;

import com.github.wnameless.spring.boot.up.permission.ability.Ability;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;

public interface ResourceAccessRule<T, ID, RF extends ResourceFilterRepository<T, ID>> {

  Class<T> getResourceType();

  default String getResourceName() {
    return getResourceType().getSimpleName();
  }

  RF getResourceFilterRepository();

  default Predicate getPredicateOfEntity(T entity) {
    for (Field field : entity.getClass().getDeclaredFields()) {
      Annotation[] annotations = field.getDeclaredAnnotationsByType(Id.class);
      if (annotations.length > 0) {
        field.setAccessible(true);
        try {
          @SuppressWarnings("unchecked")
          ID id = (ID) field.get(entity);
          return getPredicateOfEntityId(id);
        } catch (IllegalArgumentException | IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }

    throw new IllegalStateException("No Spring Data @Id found on entity");
  }

  Predicate getPredicateOfEntityId(ID id);

  Predicate getPredicateOfManageAbility();

  default Predicate getPredicateOfManageById(ID id) {
    return ExpressionUtils.allOf(getPredicateOfManageAbility(),
        getPredicateOfEntityId(id));
  }

  Predicate getPredicateOfCRUDAbility();

  default Predicate getPredicateOfCRUDById(ID id) {
    return ExpressionUtils.allOf(getPredicateOfCRUDAbility(),
        getPredicateOfEntityId(id));
  }

  int getOrder();

  default Predicate getPredicateOfCreateAbility() {
    return getPredicateOfCRUDAbility();
  }

  default Predicate getPredicateOfReadAbility() {
    return getPredicateOfCRUDAbility();
  }

  default Predicate getPredicateOfReadById(ID id) {
    return ExpressionUtils.allOf(getPredicateOfReadAbility(),
        getPredicateOfEntityId(id));
  }

  default Predicate getPredicateOfUpdateAbility() {
    return getPredicateOfCRUDAbility();
  }

  default Predicate getPredicateOfUpdateById(ID id) {
    return ExpressionUtils.allOf(getPredicateOfUpdateAbility(),
        getPredicateOfEntityId(id));
  }

  default Predicate getPredicateOfDestroyAbility() {
    return getPredicateOfCRUDAbility();
  }

  default Predicate getPredicateOfDestroyById(ID id) {
    return ExpressionUtils.allOf(getPredicateOfDestroyAbility(),
        getPredicateOfEntityId(id));
  }

  default Predicate getPredicateOfAbility(Ability ability) {
    throw new UnsupportedOperationException();
  }

  default Predicate getPredicateOfAbilityById(Ability ability, ID id) {
    return ExpressionUtils.allOf(getPredicateOfAbility(ability),
        getPredicateOfEntityId(id));
  }

}
