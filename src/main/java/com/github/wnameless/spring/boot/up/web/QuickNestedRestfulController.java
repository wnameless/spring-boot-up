package com.github.wnameless.spring.boot.up.web;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import net.sf.rubycollect4j.Ruby;

public abstract class QuickNestedRestfulController< //
    PR extends CrudRepository<P, PID> & QuerydslPredicateExecutor<P>, P extends RestfulItem<PID>, PID, //
    CR extends CrudRepository<C, CID> & QuerydslPredicateExecutor<C>, C extends RestfulItem<CID>, CID>
    implements NestedRestfulController<PR, P, PID, CR, C, CID> {

  @Autowired
  protected PR parentRepository;
  @Autowired
  protected CR childRepository;

  protected P parent;
  protected C child;
  protected Iterable<C> children;

  protected Predicate predicate;
  protected Pageable pageable;

  abstract protected void quickConfigure(ModelPolicy<P> parentPolicy, ModelPolicy<C> childPolicy,
      ModelPolicy<Iterable<C>> childrenPolicy);

  abstract protected String getParentFieldName();

  @Override
  public PR getParentRepository() {
    return parentRepository;
  }

  @Override
  public CR getChildRepository() {
    return childRepository;
  }

  @Override
  public RestfulRoute<CID> getRoute() {
    return new NestedRestfulRoute<CID>(parent.getShowPath(), child.getIndexPath());
  }

  @SuppressWarnings("unchecked")
  protected Class<P> getParentResourceType() {
    var genericTypeResolver =
        GenericTypeResolver.resolveTypeArguments(this.getClass(), NestedRestfulController.class);
    return (Class<P>) genericTypeResolver[1];
  }

  @SuppressWarnings("unchecked")
  protected Class<C> getChildResourceType() {
    var genericTypeResolver =
        GenericTypeResolver.resolveTypeArguments(this.getClass(), NestedRestfulController.class);
    return (Class<C>) genericTypeResolver[4];
  }

  protected Function<P, Predicate> getChildPredicate() {
    Class<C> childClass = getChildResourceType();
    PathBuilder<C> entityPath = new PathBuilder<>(childClass, childClass.getSimpleName());
    return parent -> entityPath.get(getParentFieldName(), getParentResourceType()).eq(parent);
  }

  @Override
  public BiPredicate<P, C> getPaternityTesting() {
    String getterName = "get" + getParentFieldName().substring(0, 1).toUpperCase()
        + getParentFieldName().substring(1);
    return (p, c) -> //
    p != null && c != null //
        && Objects.equals(p, Ruby.Object.send(c, getterName));
  }

  @Override
  public Iterable<C> getChildren(P parent) {
    return childRepository.findAll(getChildPredicate().apply(parent));
  }

  @Override
  public void configure(ModelPolicy<P> parentPolicy, ModelPolicy<C> childPolicy,
      ModelPolicy<Iterable<C>> childrenPolicy) {

    parentPolicy.forItemInitialized(parent -> this.parent = parent);
    childPolicy.forItemInitialized(child -> this.child = child);
    childrenPolicy.forItemInitialized(children -> this.children = children);

    childrenPolicy.forQueryConfig(c -> {
      this.pageable = c.getPageable();
      this.predicate = c.getPredicate();
      return c;
    });

    quickConfigure(parentPolicy, childPolicy, childrenPolicy);
  }

}
