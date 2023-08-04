package com.github.wnameless.spring.boot.up.actioncode;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import java.util.Optional;
import java.util.function.BiConsumer;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import com.github.wnameless.spring.boot.up.web.RestfulRouteProvider;
import com.github.wnameless.spring.boot.up.web.WebModelAttribute;

public interface ActionCodeController<AC extends ActionCode<A>, A extends Enum<?>, ID>
    extends RestfulRouteProvider<ID> {

  ActionCodeService<AC, A, ID> getActionCodeService();

  @GetMapping(path = "/{id}/action-codes/{actionName}/{code}")
  default ModelAndView executeAction(ModelAndView mav, @PathVariable String actionName,
      @PathVariable String code) {
    mav.setView(new RedirectView(getRestfulRoute().getIndexPath()));
    Optional<AC> actionCodeOpt = getActionCodeService()
        .findByActionAndCode(getActionCodeService().getActionEnum(actionName), code);
    if (actionCodeOpt.isPresent()) {
      execution().accept(mav, actionCodeOpt.get());
    }
    return mav;
  }

  @PostMapping(path = "/{id}/action-codes/{actionName}", consumes = APPLICATION_JSON_VALUE)
  default ModelAndView generateAction(ModelAndView mav, @PathVariable String actionName,
      @RequestParam(required = true) String ajaxTargetId) {
    mav.setViewName("action-codes/display :: bar");
    generation().accept(mav, getActionCodeService().getActionEnum(actionName));
    mav.addObject(ActionCodeAttribute.ACTION, actionName);
    mav.addObject(WebModelAttribute.AJAX_TARGET, ajaxTargetId);
    return mav;
  }

  @GetMapping(path = "/{id}/action-codes/{actionName}", consumes = APPLICATION_JSON_VALUE)
  default ModelAndView requestAction(ModelAndView mav, @PathVariable String actionName,
      @RequestParam(required = true) String ajaxTargetId) {
    mav.setViewName("action-codes/display :: bar");
    request().accept(mav, getActionCodeService().getActionEnum(actionName));
    mav.addObject(ActionCodeAttribute.ACTION, actionName);
    mav.addObject(WebModelAttribute.AJAX_TARGET, ajaxTargetId);
    return mav;
  }

  @DeleteMapping(path = "/{id}/action-codes/{actionName}/{code}", consumes = APPLICATION_JSON_VALUE)
  default ModelAndView deleteAction(ModelAndView mav, @PathVariable String actionName,
      @PathVariable String code, @RequestParam(required = true) String ajaxTargetId) {
    mav.setViewName("action-codes/display :: bar");
    Optional<AC> actionCodeOpt = getActionCodeService()
        .findByActionAndCode(getActionCodeService().getActionEnum(actionName), code);
    if (actionCodeOpt.isPresent()) {
      getActionCodeService().deleteByActionCode(actionCodeOpt.get());
      deletion().accept(mav, actionCodeOpt.get());
    }
    mav.addObject(ActionCodeAttribute.ACTION, actionName);
    mav.addObject(ActionCodeAttribute.CODE, "");
    mav.addObject(WebModelAttribute.AJAX_TARGET, ajaxTargetId);
    return mav;
  }

  BiConsumer<ModelAndView, A> request();

  BiConsumer<ModelAndView, A> generation();

  BiConsumer<ModelAndView, AC> execution();

  default BiConsumer<ModelAndView, AC> deletion() {
    return (mav, actionCode) -> {};
  }

}
