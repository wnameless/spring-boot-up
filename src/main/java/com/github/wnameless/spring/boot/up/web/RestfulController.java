package com.github.wnameless.spring.boot.up.web;

import org.springframework.data.repository.CrudRepository;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.Item;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.ItemClass;

public interface RestfulController<R extends CrudRepository<I, ID>, I, ID>
    extends RestfulRouteController<ID>, RestfulRepositoryProvider<I, ID> {

  void configure(ModelPolicy<I> policy);

  default ModelPolicy<I> getModelPolicy() {
    ModelPolicy<I> policy = new ModelPolicy<I>();
    configure(policy);
    return policy;
  }

  @ModelAttribute
  default void setItem(Model model, @PathVariable(required = false) ID id) {
    if (getModelPolicy().isDisable()) return;

    I item = null;

    if (id != null) {
      item = getRestfulRepository().findById(id).orElseGet(getModelPolicy().onDefaultItem());
    } else {
      item = getModelPolicy().onDefaultItem().get();
    }

    if (getModelPolicy().onItemInitialized() != null) {
      item = getModelPolicy().onItemInitialized().apply(item);
    }

    updateItem(model, item);
  }

  default String getItemClassKey() {
    return ItemClass.name();
  }

  default String getItemKey() {
    return Item.name();
  }

  default I getItem(ID id) {
    return getItem(id, null);
  }

  default I getItem(ID id, I defaultItem) {
    if (id != null) {
      return getRestfulRepository().findById(id).get();
    }
    return defaultItem;
  }

  default I updateItem(Model model, I item) {
    model.addAttribute(getItemKey(), item);
    if (item != null) {
      model.addAttribute(getItemClassKey(), item.getClass());
    }
    return item;
  }

  default String getQueryConfigKey() {
    return com.github.wnameless.spring.boot.up.web.ModelAttributes.QueryConfig.name();
  }

  @ModelAttribute
  default void setQueryConfig(Model model, @RequestParam MultiValueMap<String, String> params) {
    ModelHelper.forwrdAttributes(model, params);

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
