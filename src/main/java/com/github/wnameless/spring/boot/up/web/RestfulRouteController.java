package com.github.wnameless.spring.boot.up.web;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

public interface RestfulRouteController<ID> extends RestfulRouteProvider<ID> {

  default RestfulRoute<ID> getTemplateRoute() {
    return getRestfulRoute().toTemplateRoute();
  }

  default String getRouteKey() {
    return "route";
  }

  default String getTemplateRouteKey() {
    return "template";
  }

  @ModelAttribute
  default void setRoute(Model model) {
    model.addAttribute(getRouteKey(), getRestfulRoute());
  }

  @ModelAttribute
  default void setTemplateRoute(Model model) {
    model.addAttribute(getTemplateRouteKey(), getTemplateRoute());
  }

}
