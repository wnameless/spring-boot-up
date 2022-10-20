/*
 *
 * Copyright 2020 Wei-Ming Wu
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package com.github.wnameless.spring.boot.up.web;

import java.util.ArrayList;
import java.util.function.BiPredicate;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.repository.CrudRepository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.github.wnameless.spring.boot.up.SpringBootUp;

public interface NestedRestfulController< //
    PR extends CrudRepository<P, PID>, P, PID, //
    CR extends CrudRepository<C, CID>, C, CID> {

  @ModelAttribute
  default void cacheModel(HttpServletRequest req, Model model) {
    SpringBootUp.cacheWebUiModel(req, model);
  }

  Function<P, ? extends RestfulRoute<CID>> getRoute();

  PR getParentRepository();

  CR getChildRepository();

  BiPredicate<P, C> getPaternityTesting();

  Iterable<C> getChildren(P parent);

  void configure(ModelPolicy<P> parentPolicy, ModelPolicy<C> childPolicy,
      ModelPolicy<Iterable<C>> childrenPolicy);

  default ModelPolicy<P> getParentModelPolicy() {
    ModelPolicy<P> parentPolicy = new ModelPolicy<>();
    ModelPolicy<C> childPolicy = new ModelPolicy<>();
    ModelPolicy<Iterable<C>> childrenPolicy = new ModelPolicy<>();
    configure(parentPolicy, childPolicy, childrenPolicy);
    return parentPolicy;
  }

  default ModelPolicy<C> getChildModelPolicy() {
    ModelPolicy<P> parentPolicy = new ModelPolicy<>();
    ModelPolicy<C> childPolicy = new ModelPolicy<>();
    ModelPolicy<Iterable<C>> childrenPolicy = new ModelPolicy<>();
    configure(parentPolicy, childPolicy, childrenPolicy);
    return childPolicy;
  }

  default ModelPolicy<Iterable<C>> getChildrenModelPolicy() {
    ModelPolicy<P> parentPolicy = new ModelPolicy<>();
    ModelPolicy<C> childPolicy = new ModelPolicy<>();
    ModelPolicy<Iterable<C>> childrenPolicy =
        new ModelPolicy<>(new ArrayList<>());
    configure(parentPolicy, childPolicy, childrenPolicy);
    return childrenPolicy;
  }

  @ModelAttribute
  default void setParentAndChild(Model model,
      @PathVariable(required = false) PID parentId,
      @PathVariable(required = false) CID id) {
    if (getParentModelPolicy().isDisable()) return;

    P parent = null;
    if (parentId != null) {
      parent = getParentRepository().findById(parentId)
          .orElseGet(getParentModelPolicy().onDefaultItem());
    }
    if (getParentModelPolicy().onItemInitialized() != null) {
      parent = getParentModelPolicy().onItemInitialized().apply(parent);
    }
    model.addAttribute(getParentKey(), parent);

    if (getChildModelPolicy().isDisable()) return;

    C child = null;
    if (parent != null && id != null) {
      child = getChildRepository().findById(id)
          .orElseGet(getChildModelPolicy().onDefaultItem());
      child = getPaternityTesting().test(parent, child) ? child : null;
    }
    if (getChildModelPolicy().onItemInitialized() != null) {
      child = getChildModelPolicy().onItemInitialized().apply(child);
    }
    model.addAttribute(getChildKey(), child);
  }

  @ModelAttribute
  default void setChildren(Model model,
      @PathVariable(required = false) PID parentId,
      @PathVariable(required = false) CID id) {
    if (getChildrenModelPolicy().isDisable()) return;

    Iterable<C> children = null;
    if (parentId != null && id == null) {
      P parent = getParentRepository().findById(parentId)
          .orElseGet(getParentModelPolicy().onDefaultItem());
      children = getChildren(parent);
    }
    if ((children == null || !children.iterator().hasNext())
        && getChildrenModelPolicy().onDefaultItem() != null) {
      children = getChildrenModelPolicy().onDefaultItem().get();
    }
    if (getChildrenModelPolicy().onItemInitialized() != null) {
      children = getChildrenModelPolicy().onItemInitialized().apply(children);
    }
    model.addAttribute(getChildrenKey(), children);
  }

  @ModelAttribute
  default void setRoute(Model model,
      @PathVariable(required = false) PID parentId) {
    if (parentId != null) {
      model.addAttribute(getRouteKey(), getRoute().apply(getParent(parentId)));
    }
  }

  default String getRouteKey() {
    return "route";
  }

  default String getParentKey() {
    return "parent";
  }

  default P getParent(PID parentId) {
    return getParent(parentId, null);
  }

  default P getParent(PID parentId, P defaultItem) {
    if (parentId != null) {
      return getParentRepository().findById(parentId)
          .orElseGet(getParentModelPolicy().onDefaultItem());
    }
    return defaultItem;
  }

  default P updateParent(Model model, P parent) {
    model.addAttribute(getParentKey(), parent);
    return parent;
  }

  default String getChildKey() {
    return "child";
  }

  default C getChild(PID parentId, CID id) {
    return getChild(parentId, id, null);
  }

  default C getChild(PID parentId, CID id, C defaultItem) {
    if (parentId != null && id != null) {
      P parent = getParentRepository().findById(parentId)
          .orElseGet(getParentModelPolicy().onDefaultItem());
      C child = getChildRepository().findById(id)
          .orElseGet(getChildModelPolicy().onDefaultItem());
      if (getPaternityTesting().test(parent, child)) {
        return child;
      }
    }

    return defaultItem;
  }

  default C updateChild(Model model, C child) {
    model.addAttribute(getChildKey(), child);
    return child;
  }

  default String getChildrenKey() {
    return "children";
  }

  default Iterable<C> updateChildren(Model model, Iterable<C> children) {
    model.addAttribute(getChildrenKey(), children);
    return children;
  }

  default Iterable<C> updateChildrenByParent(Model model, P parent) {
    Iterable<C> children = getChildren(parent);
    model.addAttribute(getChildrenKey(), children);
    return children;
  }

}
