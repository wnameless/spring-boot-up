package com.github.wnameless.spring.boot.up.web;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

public interface HtmlRestfulWebAction<D, ID> extends BaseWebAction<D>, RestfulRouteProvider<ID> {

  @GetMapping
  default ModelAndView indexHtml(ModelAndView mav,
      @RequestParam MultiValueMap<String, String> params) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("index :: complete"));
    indexProcedure().accept(mav, params);
    return mav;
  }

  @GetMapping("/{id}")
  default ModelAndView showHtml(ModelAndView mav,
      @RequestParam MultiValueMap<String, String> params) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("show :: complete"));
    showProcedure().accept(mav, params);
    return mav;
  }

  @GetMapping("/new")
  default ModelAndView newHtml(ModelAndView mav,
      @RequestParam MultiValueMap<String, String> params) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("new :: complete"));
    newProcedure().accept(mav, params);
    return mav;
  }

  @PostMapping
  default ModelAndView createHtml(ModelAndView mav,
      @RequestParam MultiValueMap<String, String> params, @RequestBody D data) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("show :: complete"));
    createProcedure().accept(mav, params, data);
    return mav;
  }

  @GetMapping("/{id}/edit")
  default ModelAndView editHtml(ModelAndView mav,
      @RequestParam MultiValueMap<String, String> params) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("edit :: complete"));
    editProcedure().accept(mav, params);
    return mav;
  }

  @RequestMapping(path = "/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
  default ModelAndView updateHtml(ModelAndView mav,
      @RequestParam MultiValueMap<String, String> params, @RequestBody D data) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("show :: complete"));
    updateProcedure().accept(mav, params, data);
    return mav;
  }

  @DeleteMapping("/{id}")
  default ModelAndView deleteHtml(ModelAndView mav,
      @RequestParam MultiValueMap<String, String> params) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("index :: complete"));
    deleteProcedure().accept(mav, params);
    return mav;
  }

}
