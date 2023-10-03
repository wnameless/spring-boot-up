package com.github.wnameless.spring.boot.up.web;

import java.util.Objects;
import java.util.Optional;
import org.atteo.evo.inflector.English;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import com.github.wnameless.apt.INamedResource;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.google.common.base.CaseFormat;
import com.querydsl.core.types.Predicate;

public abstract class QuickRestfulController<R extends CrudRepository<I, ID>, I, ID>
    implements RestfulController<R, I, ID>, RestfulItemProvider<I> {

  @Autowired
  protected R itemRepository;

  protected I item;

  protected Predicate predicate;
  protected Pageable pageable;

  @Override
  public R getRestfulRepository() {
    return itemRepository;
  }

  @Override
  public RestfulRoute<ID> getRestfulRoute() {

    Optional<INamedResource> nr =
        SpringBootUp.findAllGenericBeans(INamedResource.class).stream().filter(n -> {
          var itemClassName = item.getClass().getName();
          String nrClassName = null;
          try {
            nrClassName = n.getClass().getDeclaredField("CLASS_NAME").get(n).toString();
          } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
              | SecurityException e) {}
          return Objects.equals(itemClassName, nrClassName);
        }).findFirst();

    if (nr.isPresent()) {
      try {
        String resourcePath =
            nr.get().getClass().getDeclaredField("RESOURCE_PATH").get(nr.get()).toString();
        if (resourcePath != null) {
          return RestfulRoute.of(resourcePath);
        }
      } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
          | SecurityException e) {}
    }

    return RestfulRoute.of("/" + English.plural(
        CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, item.getClass().getSimpleName())));
  }

  @Override
  public I getRestfulItem() {
    return item;
  }

}
