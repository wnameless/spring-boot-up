package com.github.wnameless.spring.boot.up.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.BiPredicate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import com.github.wnameless.spring.boot.up.permission.resource.ResourceFilterRepository;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.Child;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.ChildClass;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.Children;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.Parent;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.ParentClass;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.Route;

public interface NestedRestfulController< //
    PR extends CrudRepository<P, PID>, P, PID, //
    CR extends CrudRepository<C, CID>, C, CID> {

  default Optional<P> findParentItemById(PID id) {
    Optional<P> item;
    if ((CrudRepository<P, PID>) getParentRepository() instanceof ResourceFilterRepository<P, PID> rfr) {
      item = rfr.filterFindById(id);
    } else {
      item = getParentRepository().findById(id);
    }
    return item;
  }

  default Optional<C> findChildItemById(CID id) {
    Optional<C> item;
    if ((CrudRepository<C, CID>) getChildRepository() instanceof ResourceFilterRepository<C, CID> rfr) {
      item = rfr.filterFindById(id);
    } else {
      item = getChildRepository().findById(id);
    }
    return item;
  }

  RestfulRoute<CID> getRoute();

  PR getParentRepository();

  CR getChildRepository();

  BiPredicate<P, C> getPaternityTesting();

  Iterable<C> getChildren(P parent);

  void configure(ModelPolicy<P> parentPolicy, ModelPolicy<C> childPolicy,
      ModelPolicy<Iterable<C>> childrenPolicy);

  default ModelPolicy<P> getParentModelPolicy() {
    ModelPolicy<P> parentPolicy = new ModelPolicy<>();
    ModelPolicy<C> childPolicy = new ModelPolicy<>();
    ModelPolicy<Iterable<C>> childrenPolicy = new ModelPolicy<>(new ArrayList<>());
    configure(parentPolicy, childPolicy, childrenPolicy);
    return parentPolicy;
  }

  default ModelPolicy<C> getChildModelPolicy() {
    ModelPolicy<P> parentPolicy = new ModelPolicy<>();
    ModelPolicy<C> childPolicy = new ModelPolicy<>();
    ModelPolicy<Iterable<C>> childrenPolicy = new ModelPolicy<>(new ArrayList<>());
    configure(parentPolicy, childPolicy, childrenPolicy);
    return childPolicy;
  }

  default ModelPolicy<Iterable<C>> getChildrenModelPolicy() {
    ModelPolicy<P> parentPolicy = new ModelPolicy<>();
    ModelPolicy<C> childPolicy = new ModelPolicy<>();
    ModelPolicy<Iterable<C>> childrenPolicy = new ModelPolicy<>(new ArrayList<>());
    configure(parentPolicy, childPolicy, childrenPolicy);
    return childrenPolicy;
  }

  @ModelAttribute
  default void setParentAndChildAndRouteAndChildren(Model model,
      @PathVariable(required = false) PID parentId, @PathVariable(required = false) CID id) {
    // Set Parent
    if (getParentModelPolicy().isDisable()) return;

    P parent = null;
    if (parentId != null) {
      parent = findParentItemById(parentId).orElseGet(getParentModelPolicy().onDefaultItem());
    } else {
      parent = getParentModelPolicy().onDefaultItem().get();
    }
    if (getParentModelPolicy().onItemInitialized() != null) {
      parent = getParentModelPolicy().onItemInitialized().apply(parent);
    }
    model.addAttribute(getParentKey(), parent);

    // Set Child
    if (getChildModelPolicy().isDisable()) return;

    C child = null;
    if (parent != null) {
      if (id != null) {
        child = findChildItemById(id).orElseGet(getChildModelPolicy().onDefaultItem());
      } else {
        child = getChildModelPolicy().onDefaultItem().get();
      }
      child = getPaternityTesting().test(parent, child) ? child : null;
    }
    if (getChildModelPolicy().onItemInitialized() != null) {
      child = getChildModelPolicy().onItemInitialized().apply(child);
    }
    model.addAttribute(getChildKey(), child);

    // Set Route
    if (parent != null && child != null) {
      model.addAttribute(getRouteKey(), getRoute());
    }

    // Set Children
    if (getChildrenModelPolicy().isDisable() || parent == null) return;

    Iterable<C> children = getChildren(parent);
    if ((children == null || !children.iterator().hasNext())
        && getChildrenModelPolicy().onDefaultItem() != null) {
      children = getChildrenModelPolicy().onDefaultItem().get();
    }
    if (getChildrenModelPolicy().onItemInitialized() != null) {
      children = getChildrenModelPolicy().onItemInitialized().apply(children);
    }
    model.addAttribute(getChildrenKey(), children);
  }

  default String getRouteKey() {
    return Route.name();
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
      return findParentItemById(parentId).orElseGet(getParentModelPolicy().onDefaultItem());
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

  default String getChildClassKey() {
    return ChildClass.name();
  }

  default String getChildKey() {
    return Child.name();
  }

  default C getChild(PID parentId, CID id) {
    return getChild(parentId, id, null);
  }

  default C getChild(PID parentId, CID id, C defaultItem) {
    if (parentId != null && id != null) {
      P parent = findParentItemById(parentId).orElseGet(getParentModelPolicy().onDefaultItem());
      C child = findChildItemById(id).orElseGet(getChildModelPolicy().onDefaultItem());
      if (getPaternityTesting().test(parent, child)) {
        return child;
      }
    }

    return defaultItem;
  }

  default C updateChild(Model model, C child) {
    model.addAttribute(getChildKey(), child);
    if (child != null) {
      model.addAttribute(getChildClassKey(), child.getClass());
    }
    return child;
  }

  default String getChildrenKey() {
    return Children.name();
  }

  default Iterable<C> updateChildren(Model model, Iterable<C> children) {
    model.addAttribute(getChildrenKey(), children);
    Iterator<C> iter = children.iterator();
    if (iter.hasNext()) {
      model.addAttribute(getChildClassKey(), iter.next().getClass());
    }
    return children;
  }

  default Iterable<C> updateChildrenByParent(Model model, P parent) {
    Iterable<C> children = getChildren(parent);
    updateChildren(model, children);
    return children;
  }

  default String getQueryConfigKey() {
    return com.github.wnameless.spring.boot.up.web.ModelAttributes.QueryConfig.name();
  }

  @ModelAttribute
  default void setQueryConfig(Model model, @RequestParam MultiValueMap<String, String> params) {
    ModelHelper.forwrdAttributes(model, params);

    if (getChildrenModelPolicy().isDisable()) return;

    if (getChildrenModelPolicy().onQueryConfig() != null) {
      QueryConfig<?> queryConfig =
          new QueryConfig<>(getChildrenModelPolicy().onQuerySetting().get(), params);
      if (getChildrenModelPolicy().onQueryConfig() != null) {
        queryConfig = getChildrenModelPolicy().onQueryConfig().apply(queryConfig);
      }
      model.addAttribute(getQueryConfigKey(), queryConfig);
    }
  }

}
