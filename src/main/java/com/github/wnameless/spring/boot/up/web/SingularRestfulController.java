package com.github.wnameless.spring.boot.up.web;

import java.util.Optional;
import java.util.function.Function;
import org.springframework.data.repository.CrudRepository;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

public interface SingularRestfulController<R extends CrudRepository<I, ID>, I, ID>
    extends RestfulRouteController<Void> {

  @Override
  SingularRestfulRoute getRestfulRoute();

  R getRepository();

  void configure(ModelPolicy<I> policy);

  default ModelPolicy<I> getModelPolicy() {
    ModelPolicy<I> policy = new ModelPolicy<I>();
    configure(policy);
    return policy;
  }

  @ModelAttribute
  default void setItem(Model model) {
    if (getModelPolicy().isDisable()) return;

    I item = itemStrategy().apply(getRepository()).orElseGet(getModelPolicy().onDefaultItem());

    if (getModelPolicy().onItemInitialized() != null) {
      item = getModelPolicy().onItemInitialized().apply(item);
    }

    model.addAttribute(getItemKey(), item);
    if (item != null) {
      model.addAttribute(getItemClassKey(), item.getClass());
    }
  }

  default String getItemClassKey() {
    return WebModelAttribute.ITEM_CLASS;
  }

  Function<R, Optional<I>> itemStrategy();

  default String getItemKey() {
    return WebModelAttribute.ITEM;
  }

  default I updateItem(Model model, I item) {
    model.addAttribute(getItemKey(), item);
    return item;
  }

  default String getQueryConfigKey() {
    return WebModelAttribute.QUERY_CONFIG;
  }

  @ModelAttribute
  default void setQueryConfig(Model model, @RequestParam MultiValueMap<String, String> params) {
    if (getModelPolicy().isDisable()) return;

    if (getModelPolicy().onQueryConfig() != null) {
      QueryConfig<?> queryConfig =
          new QueryConfig<>(getModelPolicy().onQuerySetting().get(), params);
      if (getModelPolicy().onQueryConfig() != null) {
        queryConfig = getModelPolicy().onQueryConfig().apply(queryConfig);
      }
      model.addAttribute(getQueryConfigKey(), queryConfig);
    }
  }

}
