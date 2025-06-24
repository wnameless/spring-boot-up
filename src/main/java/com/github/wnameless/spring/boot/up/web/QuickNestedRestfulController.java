package com.github.wnameless.spring.boot.up.web;

import java.util.Objects;
import java.util.function.BiPredicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import com.querydsl.core.types.Predicate;
import lombok.SneakyThrows;
import net.sf.rubycollect4j.Ruby;

public abstract class QuickNestedRestfulController<PR extends CrudRepository<P, PID> & QuerydslPredicateExecutor<P>, P extends RestfulItem<PID>, PID, //
    R extends CrudRepository<I, ID> & QuerydslPredicateExecutor<I>, I extends RestfulItem<ID>, ID>
    implements NestedRestfulController<PR, P, PID, R, I, ID> {

  @Autowired
  protected PR parentRepository;
  @Autowired
  protected R itemRepository;

  protected P parent;
  protected I item;

  protected Predicate predicate;
  protected Pageable pageable;

  abstract protected void quickConfigure(ModelPolicy<I> policy);

  abstract protected String getParentFieldName();

  @Override
  public PR getParentRepository() {
    return parentRepository;
  }

  @Override
  public R getRestfulRepository() {
    return itemRepository;
  }

  @Override
  public RestfulRoute<ID> getRestfulRoute() {
    return new NestedRestfulRoute<ID>(parent.getShowPath(), item.getIndexPath());
  }

  @SuppressWarnings("unchecked")
  protected Class<P> getParentItemType() {
    var genericTypeResolver = GenericTypeResolver
        .resolveTypeArguments(getParentRepository().getClass(), CrudRepository.class);
    return (Class<P>) genericTypeResolver[0];
  }

  @SneakyThrows
  protected P newParentItem() {
    return getParentItemType().getDeclaredConstructor().newInstance();
  }

  @SuppressWarnings("unchecked")
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
  public BiPredicate<P, I> getPaternityTesting() {
    String getterName = "get" + getParentFieldName().substring(0, 1).toUpperCase()
        + getParentFieldName().substring(1);
    return (p, c) -> //
    p != null && c != null //
        && Objects.equals(p, Ruby.Object.send(c, getterName));
  }

  @SuppressWarnings("unchecked")
  @Override
  public void configure(ModelPolicy<I> policy) {
    policy.forDefaultItem(() -> newRestfulItem());

    policy.forItemInitialized(child -> this.item = child);
    policy.forParentInitialized(parent -> this.parent = (P) parent);

    policy.forQueryConfig(c -> {
      this.pageable = c.getPageable();
      this.predicate = c.getPredicate();
      return c;
    });

    quickConfigure(policy);
  }

}
