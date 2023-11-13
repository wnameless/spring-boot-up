package com.github.wnameless.spring.boot.up.web;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

public interface HtmlRestfulWebAction<D, ID>
    extends BaseWebAction<D, ID>, RestfulRouteProvider<ID> {

  @GetMapping
  default ModelAndView indexHtml(ModelAndView mav,
      @RequestParam MultiValueMap<String, String> params) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("index :: complete"));
    indexProcedure().accept(mav, params);
    return mav;
  }

  @GetMapping("/{id}")
  default ModelAndView showHtml(@PathVariable ID id, ModelAndView mav,
      @RequestParam MultiValueMap<String, String> params) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("show :: complete"));
    showProcedure().accept(id, mav, params);
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
  default ModelAndView editHtml(@PathVariable ID id, ModelAndView mav,
      @RequestParam MultiValueMap<String, String> params) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("edit :: complete"));
    editProcedure().accept(id, mav, params);
    return mav;
  }

  @RequestMapping(path = "/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
  default ModelAndView updateHtml(@PathVariable ID id, ModelAndView mav,
      @RequestParam MultiValueMap<String, String> params, @RequestBody D data) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("show :: complete"));
    updateProcedure().accept(id, mav, params, data);
    return mav;
  }

  @DeleteMapping("/{id}")
  default ModelAndView deleteHtml(@PathVariable ID id, ModelAndView mav,
      @RequestParam MultiValueMap<String, String> params) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("index :: complete"));
    deleteProcedure().accept(id, mav, params);
    return mav;
  }

}
