package com.github.wnameless.spring.boot.up.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import com.querydsl.core.types.Predicate;
import lombok.SneakyThrows;

public abstract class QuickNestedSingularRestfulController<PR extends CrudRepository<P, PID> & QuerydslPredicateExecutor<P>, P extends RestfulItem<PID>, PID, //
    R extends CrudRepository<I, ID> & QuerydslPredicateExecutor<I>, I extends RestfulItem<ID>, ID>
    implements NestedSinglularRestfulController<PR, P, PID, R, I, ID> {

  @Autowired
  protected PR parentRepository;
  @Autowired
  protected R itemRepository;

  protected P parent;
  protected I item;

  protected Predicate predicate;
  protected Pageable pageable;

  abstract protected void quickConfigure(ModelPolicy<I> policy);

  @Override
  public PR getParentRepository() {
    return parentRepository;
  }

  @Override
  public R getRestfulRepository() {
    return itemRepository;
  }

  @SuppressWarnings({"unchecked", "null"})
  protected Class<I> getRestfulItemType() {
    var genericTypeResolver = GenericTypeResolver
        .resolveTypeArguments(getRestfulRepository().getClass(), CrudRepository.class);
    return (Class<I>) genericTypeResolver[0];
  }

  @SneakyThrows
  protected I newRestfulItem() {
    return getRestfulItemType().getDeclaredConstructor().newInstance();
  }

  @Override
  public void configure(ModelPolicy<I> policy) {
    policy.forDefaultItem(() -> newRestfulItem());

    policy.forItemInitialized(item -> this.item = item);

    policy.forQueryConfig(c -> {
      this.pageable = c.getPageable();
      this.predicate = c.getPredicate();
      return c;
    });

    quickConfigure(policy);
  }

}
