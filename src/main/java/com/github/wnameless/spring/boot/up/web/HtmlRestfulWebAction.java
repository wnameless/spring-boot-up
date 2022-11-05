package com.github.wnameless.spring.boot.up.web;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

public interface HtmlRestfulWebAction<D, ID>
    extends BaseWebAction<D>, RestfulRouteProvider<ID> {

  @GetMapping
  default ModelAndView indexHtml(ModelAndView mav) {
    mav.setViewName(
        getRestfulRoute().toTemplateRoute().joinPath("index :: complete"));
    indexAction(mav);
    return mav;
  }

  @GetMapping("/{id}")
  default ModelAndView showHtml(ModelAndView mav) {
    mav.setViewName(
        getRestfulRoute().toTemplateRoute().joinPath("show :: complete"));
    showAction(mav);
    return mav;
  }

  @GetMapping("/new")
  default ModelAndView newHtml(ModelAndView mav) {
    mav.setViewName(
        getRestfulRoute().toTemplateRoute().joinPath("new :: complete"));
    newAction(mav);
    return mav;
  }

  @PostMapping
  default ModelAndView createHtml(ModelAndView mav, @RequestBody D data) {
    mav.setViewName(
        getRestfulRoute().toTemplateRoute().joinPath("index :: complete"));
    createAction(mav, data);
    return mav;
  }

  @GetMapping("/{id}/edit")
  default ModelAndView editHtml(ModelAndView mav) {
    mav.setViewName(
        getRestfulRoute().toTemplateRoute().joinPath("edit :: complete"));
    editAction(mav);
    return mav;
  }

  @PostMapping("/{id}")
  default ModelAndView updateHtml(ModelAndView mav, @RequestBody D data) {
    mav.setViewName(
        getRestfulRoute().toTemplateRoute().joinPath("index :: complete"));
    updateAction(mav, data);
    return mav;
  }

  @DeleteMapping("/{id}")
  default ModelAndView deleteHtml(ModelAndView mav) {
    mav.setViewName(
        getRestfulRoute().toTemplateRoute().joinPath("index :: complete"));
    deleteAction(mav);
    return mav;
  }

}
