package com.github.wnameless.spring.boot.up.tagging;

import java.util.Collection;
import java.util.List;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface LabelTemplateRepository<L extends LabelTemplate<ID>, ID>
    extends CrudRepository<L, ID>, QuerydslPredicateExecutor<L> {

  List<L> findAllByEntityType(String entityType);

  List<L> findAllByEntityTypeAndIdIn(String entityType, Collection<ID> ids);

  // List<L> findAllByEntityTypeAndUsernameAndIdIn(String entityType, String username,
  // Collection<ID> ids);

}
