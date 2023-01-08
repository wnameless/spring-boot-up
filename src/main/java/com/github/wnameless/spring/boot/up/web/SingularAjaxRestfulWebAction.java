package com.github.wnameless.spring.boot.up.web;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

public interface SingularAjaxRestfulWebAction<D, ID>
    extends SingularBaseWebAction<D>, RestfulRouteProvider<Void> {

  @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView showAjax(ModelAndView mav) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("show :: partial"));
    showAction(mav);
    return mav;
  }

  @GetMapping(path = "/new", consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView newAjax(ModelAndView mav) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("new :: partial"));
    newAction(mav);
    return mav;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView createAjax(ModelAndView mav, @RequestBody D data) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("show :: partial"));
    createAction(mav, data);
    return mav;
  }

  @GetMapping(path = "edit", consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView editAjax(ModelAndView mav) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("edit :: partial"));
    editAction(mav);
    return mav;
  }

  @RequestMapping(method = {RequestMethod.PUT, RequestMethod.PATCH},
      consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView updateAjax(ModelAndView mav, @RequestBody D data) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("show :: partial"));
    updateAction(mav, data);
    return mav;
  }

  @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView deleteAjax(ModelAndView mav) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("show :: partial"));
    deleteAction(mav);
    return mav;
  }

}
