package com.github.wnameless.spring.boot.up.tagging;

import java.util.Collection;
import java.util.List;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface UserLabelTemplateRepository<UL extends UserLabelTemplate<ID>, ID>
    extends CrudRepository<UL, ID>, QuerydslPredicateExecutor<UL> {

  List<UL> findAllByEntityType(String entityType);

  List<UL> findAllByEntityTypeAndUsername(String entityType, String username);

  List<UL> findAllByEntityTypeAndIdIn(String entityType, Collection<ID> ids);

  // List<UL> findAllByEntityTypeAndUsernameAndIdIn(String entityType, String username,
  // Collection<ID> ids);

}
