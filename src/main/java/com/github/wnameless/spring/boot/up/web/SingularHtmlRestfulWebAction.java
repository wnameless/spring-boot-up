package com.github.wnameless.spring.boot.up.web;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

public interface SingularHtmlRestfulWebAction<D, ID>
    extends BaseWebAction<D>, RestfulRouteProvider<Void> {

  default ModelAndView indexHtml(ModelAndView mav) {
    return mav;
  }

  @GetMapping
  default ModelAndView showHtml(ModelAndView mav) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("show :: complete"));
    showAction(mav);
    return mav;
  }

  @GetMapping("/new")
  default ModelAndView newHtml(ModelAndView mav) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("new :: complete"));
    newAction(mav);
    return mav;
  }

  @PostMapping
  default ModelAndView createHtml(ModelAndView mav, @RequestBody D data) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("show :: complete"));
    createAction(mav, data);
    return mav;
  }

  @GetMapping("edit")
  default ModelAndView editHtml(ModelAndView mav) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("edit :: complete"));
    editAction(mav);
    return mav;
  }

  @RequestMapping(method = {RequestMethod.PUT, RequestMethod.PATCH})
  default ModelAndView updateHtml(ModelAndView mav, @RequestBody D data) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("show :: complete"));
    updateAction(mav, data);
    return mav;
  }

  @DeleteMapping
  default ModelAndView deleteHtml(ModelAndView mav) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("show :: complete"));
    deleteAction(mav);
    return mav;
  }

}