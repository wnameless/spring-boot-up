package com.github.wnameless.spring.boot.up.permission;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.github.wnameless.spring.boot.up.SpringBootUp;

@RequestMapping("/user-permissions")
public interface UserPermissionControllerTemplate<ID> {

  @SuppressWarnings("unchecked")
  @PostMapping("/can/{action}/to/{resourceName}/on/{resourceId}")
  default String canDoActionToResourceOnId(@RequestParam("content") String domString,
      @RequestParam(name = "classes", required = false) String classes, @PathVariable String action,
      @PathVariable String resourceName, @PathVariable String resourceId) {
    var userOpt = SpringBootUp.findBean(PermittedUser.class);

    if (userOpt.isEmpty()) return domString;
    var user = userOpt.get();

    boolean canDo;
    switch (action) {
      case "MANAGE" -> canDo = user.canManageOn(resourceName, paramToResourceId(resourceId));
      case "CRUD" -> canDo = user.canCRUDOn(resourceName, paramToResourceId(resourceId));
      case "CREATE" -> canDo = user.canCreate(resourceName);
      case "READ" -> canDo = user.canReadOn(resourceName, paramToResourceId(resourceId));
      case "UPDATE" -> canDo = user.canUpdateOn(resourceName, paramToResourceId(resourceId));
      case "DELETE" -> canDo = user.canDeleteOn(resourceName, paramToResourceId(resourceId));
      default -> canDo = user.canDoOn(action, resourceName, paramToResourceId(resourceId));

    }

    Document document = Jsoup.parse(domString);
    Element rootElement = document.body().children().first();
    if (canDo) {
      if (rootElement != null) {
        processPermittedElement(rootElement, classes);
      }
    } else {
      if (rootElement != null) {
        processForbiddenElement(rootElement, classes);
      }
    }
    return document.body().html();
  }

  Element processPermittedElement(Element element, String classes);

  Element processForbiddenElement(Element element, String classes);

  ID paramToResourceId(String resourceId);

}
