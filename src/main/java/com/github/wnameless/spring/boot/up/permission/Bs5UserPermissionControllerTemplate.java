package com.github.wnameless.spring.boot.up.permission;

import org.jsoup.nodes.Element;

public interface Bs5UserPermissionControllerTemplate<ID>
    extends UserPermissionControllerTemplate<ID> {

  default Element processPermittedElement(Element element, String classes) {
    if (classes != null) element.removeClass("disabled");
    element.addClass(classes);
    return element;
  }

  default Element processForbiddenElement(Element element, String classes) {
    element.addClass("disabled");
    return element;
  }

}
