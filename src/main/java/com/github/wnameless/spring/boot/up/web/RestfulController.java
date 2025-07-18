package com.github.wnameless.spring.boot.up.web;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import com.github.wnameless.spring.boot.up.permission.resource.ResourceFilterRepository;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.Item;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.ItemClass;
import com.github.wnameless.spring.boot.up.web.utils.EntityUtils;

public interface RestfulController<R extends CrudRepository<I, ID>, I, ID>
    extends RestfulRouteController<ID>, RestfulRepositoryProvider<I, ID> {

  default boolean isStrictPermissionEnabled() {
    return false;
  }

  default Optional<I> findRestfulItemById(ID id) {
    Optional<I> item = Optional.empty();

    if (getRestfulRepository() instanceof ResourceFilterRepository<I, ID> rfr) {
      try {
        item = rfr.filterFindById(id);

        if (item.isEmpty()) {
          item = getRestfulRepository().findById(id);
          if (item.isPresent() && item.get() instanceof I src) {
            item = EntityUtils.tryDuplicateIdOnlyEntity(src);
          }
        }
      } catch (UnsupportedOperationException e) {
        if (isStrictPermissionEnabled()) throw e;

        item = getRestfulRepository().findById(id);
        if (item.isPresent() && item.get() instanceof I src) {
          item = EntityUtils.tryDuplicateIdOnlyEntity(src);
        }
      }
    } else {
      item = getRestfulRepository().findById(id);
    }

    return item;
  }

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
      item = findRestfulItemById(id).orElseGet(getModelPolicy().onDefaultItem());
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
      return findRestfulItemById(id).get();
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
