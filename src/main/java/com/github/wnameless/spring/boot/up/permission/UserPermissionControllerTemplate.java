package com.github.wnameless.spring.boot.up.permission;

import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.github.wnameless.spring.boot.up.SpringBootUp;

@RequestMapping("/user-permissions")
public interface UserPermissionControllerTemplate<ID> {

  @SuppressWarnings("unchecked")
  @ResponseBody
  @PostMapping("/can/{action}/to/{resourceName}/on/{resourceIdStr}")
  default String canDoActionToResourceOnId(@RequestParam("htmlElement") String htmlElement,
      @RequestParam(name = "classesOnPermitted", required = false) String classesOnPermitted,
      @RequestParam(name = "classesOnForbidden", required = false) String classesOnForbidden,
      @PathVariable String action, @PathVariable String resourceName,
      @PathVariable String resourceIdStr) {
    var userOpt = SpringBootUp.findBean(PermittedUser.class);

    if (userOpt.isEmpty()) return htmlElement;
    var user = userOpt.get();

    var resourceType = user.getResourceType(resourceName);
    ID id = resourceIdStrToId(resourceIdStr);
    boolean canDo;

    var repoOpt = SpringBootUp.findGenericBean(CrudRepository.class, resourceType, id.getClass());
    if (repoOpt.isPresent()) {
      var obj = repoOpt.get().findById(id);
      switch (action) {
        case "MANAGE" -> canDo = user.canManageOn(obj, id);
        case "CRUD" -> canDo = user.canCRUDOn(obj, id);
        case "CREATE" -> canDo = user.canCreate(obj);
        case "READ" -> canDo = user.canReadOn(obj, id);
        case "UPDATE" -> canDo = user.canUpdateOn(obj, id);
        case "DELETE" -> canDo = user.canDeleteOn(obj, id);
        default -> canDo = user.canDoOn(action, obj, id);
      }
    } else {
      switch (action) {
        case "MANAGE" -> canDo = user.canManageOn(resourceName, id);
        case "CRUD" -> canDo = user.canCRUDOn(resourceName, id);
        case "CREATE" -> canDo = user.canCreate(resourceName);
        case "READ" -> canDo = user.canReadOn(resourceName, id);
        case "UPDATE" -> canDo = user.canUpdateOn(resourceName, id);
        case "DELETE" -> canDo = user.canDeleteOn(resourceName, id);
        default -> canDo = user.canDoOn(action, resourceName, id);
      }
    }

    Document document = Jsoup.parse(htmlElement);
    Element rootElement = document.body().children().first();
    if (rootElement == null) return document.body().html();

    if (canDo) {
      List.of(String.valueOf(classesOnForbidden).strip().split("\\s+")).forEach(className -> {
        rootElement.removeClass(className);
      });
      List.of(String.valueOf(classesOnPermitted).strip().split("\\s+")).forEach(className -> {
        rootElement.addClass(className);
      });
    } else {
      List.of(String.valueOf(classesOnPermitted).strip().split("\\s+")).forEach(className -> {
        rootElement.removeClass(className);
      });
      List.of(String.valueOf(classesOnForbidden).strip().split("\\s+")).forEach(className -> {
        rootElement.addClass(className);
      });
    }
    return document.body().html();
  }

  ID resourceIdStrToId(String resourceIdStr);

}
