package com.github.wnameless.spring.boot.up.actioncode;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import java.util.Optional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.AjaxTargetId;
import com.github.wnameless.spring.boot.up.web.RestfulItemProvider;
import com.github.wnameless.spring.boot.up.web.RestfulRouteProvider;

public interface ActionCodeController<AC extends ActionCode<A, T>, A extends Enum<?>, T, ID>
    extends RestfulItemProvider<T>, RestfulRouteProvider<ID> {

  ActionCodeService<AC, A, T, ID> getActionCodeService();

  @GetMapping(path = "/{id}/action-codes/{actionName}/{code}")
  default ModelAndView executeAction(ModelAndView mav, @PathVariable String actionName,
      @PathVariable String code) {
    mav.setView(new RedirectView(getRestfulRoute().getIndexPath()));
    Optional<AC> actionCodeOpt = getActionCodeService().getActionCodeRepository()
        .findByActionAndCode(getActionCodeService().getActionEnum(actionName), code);
    if (actionCodeOpt.isPresent()) {
      mav = getActionCodeService().actionCodeExecution().apply(mav, actionCodeOpt.get(),
          getRestfulItem());
    }
    mav.addObject(ActionCodeAttributes.ACTION, actionName);
    return mav;
  }

  @PostMapping(path = "/{id}/action-codes/{actionName}", consumes = APPLICATION_JSON_VALUE)
  default ModelAndView generateAction(ModelAndView mav, @PathVariable String actionName,
      @RequestParam(required = true) String ajaxTargetId) {
    mav.setViewName("sbu/action-codes/display :: bar");
    mav = getActionCodeService().actionCodeGeneration().apply(mav,
        getActionCodeService().getActionEnum(actionName), getRestfulItem());
    mav.addObject(ActionCodeAttributes.ACTION, actionName);
    mav.addObject(AjaxTargetId.name(), ajaxTargetId);
    return mav;
  }

  @GetMapping(path = "/{id}/action-codes/{actionName}", consumes = APPLICATION_JSON_VALUE)
  default ModelAndView requestAction(ModelAndView mav, @PathVariable String actionName,
      @RequestParam(required = true) String ajaxTargetId) {
    mav.setViewName("sbu/action-codes/display :: bar");
    mav = getActionCodeService().actionCodeRequest().apply(mav,
        getActionCodeService().getActionEnum(actionName), getRestfulItem());
    mav.addObject(ActionCodeAttributes.ACTION, actionName);
    mav.addObject(AjaxTargetId.name(), ajaxTargetId);
    return mav;
  }

  @DeleteMapping(path = "/{id}/action-codes/{actionName}/{code}", consumes = APPLICATION_JSON_VALUE)
  default ModelAndView deleteAction(ModelAndView mav, @PathVariable String actionName,
      @PathVariable String code, @RequestParam(required = true) String ajaxTargetId) {
    mav.setViewName("sbu/action-codes/display :: bar");
    Optional<AC> actionCodeOpt = getActionCodeService().getActionCodeRepository()
        .findByActionAndCode(getActionCodeService().getActionEnum(actionName), code);
    if (actionCodeOpt.isPresent()) {
      mav = getActionCodeService().actionCodeDeletion().apply(mav, actionCodeOpt.get(),
          getRestfulItem());
    }
    mav.addObject(ActionCodeAttributes.ACTION, actionName);
    mav.addObject(ActionCodeAttributes.CODE, "");
    mav.addObject(AjaxTargetId.name(), ajaxTargetId);
    return mav;
  }

}
