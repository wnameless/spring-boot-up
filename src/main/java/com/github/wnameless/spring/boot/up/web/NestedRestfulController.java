package com.github.wnameless.spring.boot.up.web;

import java.util.Optional;
import java.util.function.BiPredicate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import com.github.wnameless.spring.boot.up.permission.resource.ResourceFilterRepository;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.Item;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.ItemClass;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.Parent;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.ParentClass;

public interface NestedRestfulController<PR extends CrudRepository<P, PID>, P, PID, R extends CrudRepository<I, ID>, I, ID>
    extends RestfulRouteController<ID>, RestfulRepositoryProvider<I, ID> {

  default Optional<P> findParentItemById(PID id) {
    Optional<P> parent;
    if ((CrudRepository<P, PID>) getParentRepository() instanceof ResourceFilterRepository<P, PID> rfr) {
      // TODO: Fix bug
      // item = rfr.filterFindById(id);
      parent = getParentRepository().findById(id);
    } else {
      parent = getParentRepository().findById(id);
    }
    return parent;
  }

  default Optional<I> findRestfulItemById(ID id) {
    Optional<I> item;
    if ((CrudRepository<I, ID>) getRestfulRepository() instanceof ResourceFilterRepository<I, ID> rfr) {
      // TODO: Fix bug
      // item = rfr.filterFindById(id);
      item = getRestfulRepository().findById(id);
    } else {
      item = getRestfulRepository().findById(id);
    }
    return item;
  }

  PR getParentRepository();

  BiPredicate<P, I> getPaternityTesting();

  void configure(ModelPolicy<I> policy);

  default ModelPolicy<I> getModelPolicy() {
    ModelPolicy<I> policy = new ModelPolicy<>();
    configure(policy);
    return policy;
  }

  @ModelAttribute
  default void setParentAndItem(Model model, @PathVariable(required = false) PID parentId,
      @PathVariable(required = false) ID id) {
    // Set Parent
    P parent = null;
    if (parentId != null) {
      parent = findParentItemById(parentId).orElse(null);
    }
    updateParent(model, parent);

    // Set Item
    if (getModelPolicy().isDisable()) return;

    I item = null;
    if (parent != null) {
      if (id != null) {
        item = findRestfulItemById(id).orElseGet(getModelPolicy().onDefaultItem());
      } else {
        item = getModelPolicy().onDefaultItem().get();
      }
      item = getPaternityTesting().test(parent, item) ? item : null;
    }
    if (getModelPolicy().onItemInitialized() != null) {
      item = getModelPolicy().onItemInitialized().apply(item);
    }
    updateItem(model, item);
  }

  default String getParentClassKey() {
    return ParentClass.name();
  }

  default String getParentKey() {
    return Parent.name();
  }

  default P getParent(PID parentId) {
    return getParent(parentId, null);
  }

  default P getParent(PID parentId, P defaultItem) {
    if (parentId != null) {
      return findParentItemById(parentId).orElse(defaultItem);
    }
    return defaultItem;
  }

  default P updateParent(Model model, P parent) {
    model.addAttribute(getParentKey(), parent);
    if (parent != null) {
      model.addAttribute(getParentClassKey(), parent.getClass());
    }
    return parent;
  }

  default String getItemClassKey() {
    return ItemClass.name();
  }

  default String getItemKey() {
    return Item.name();
  }

  default I getItem(PID parentId, ID id) {
    return getItem(parentId, id, null);
  }

  default I getItem(PID parentId, ID id, I defaultItem) {
    if (parentId != null && id != null) {
      P parent = findParentItemById(parentId).orElse(null);
      I item = findRestfulItemById(id).orElseGet(getModelPolicy().onDefaultItem());
      if (parent == null || getPaternityTesting().test(parent, item)) {
        return item;
      }
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
