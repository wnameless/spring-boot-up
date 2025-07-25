package com.github.wnameless.spring.boot.up.web;

import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.repository.CrudRepository;
import com.github.wnameless.apt.INamedResource;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.google.common.base.CaseFormat;
import lombok.SneakyThrows;

public abstract class QuickSingularRestfulController<R extends CrudRepository<I, ID>, I, ID>
    implements SingularRestfulController<R, I, ID>, RestfulItemProvider<I>, TemplateFragmentAware {

  @Autowired
  protected R itemRepository;

  protected I item;

  abstract protected void quickConfigure(ModelPolicy<I> policy);

  @SuppressWarnings("unchecked")
  protected Class<I> getRestfulItemType() {
    var genericTypeResolver =
        GenericTypeResolver.resolveTypeArguments(itemRepository.getClass(), CrudRepository.class);
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

    quickConfigure(policy);
  }

  @Override
  public R getRestfulRepository() {
    return itemRepository;
  }

  @Override
  public SingularRestfulRoute<ID> getRestfulRoute() {
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
          return SingularRestfulRoute.of(resourcePath);
        }
      } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
          | SecurityException e) {}
    }

    return SingularRestfulRoute.of(
        "/" + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, item.getClass().getSimpleName()));
  }

  @Override
  public I getRestfulItem() {
    return item;
  }

  @Override
  public String getFragmentName() {
    return "bs5";
  }

}
