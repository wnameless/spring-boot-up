package com.github.wnameless.spring.boot.up.web;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.Route;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.TemplateRoute;

public interface RestfulRouteController<ID> extends RestfulRouteProvider<ID> {

  default RestfulRoute<ID> getTemplateRoute() {
    return getRestfulRoute().toTemplateRoute();
  }

  default String getRouteKey() {
    return Route.name();
  }

  default String getTemplateRouteKey() {
    return TemplateRoute.name();
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
