package com.github.wnameless.spring.boot.up.web;

import org.springframework.http.MediaType;
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

public interface AjaxRestfulWebAction<D, ID>
    extends BaseWebAction<D, ID>, RestfulRouteProvider<ID> {

  @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView indexAjax(ModelAndView mav,
      @RequestParam MultiValueMap<String, String> params) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("index :: partial"));
    indexProcedure().accept(mav, params);
    return mav;
  }

  @GetMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView showAjax(@PathVariable ID id, ModelAndView mav,
      @RequestParam MultiValueMap<String, String> params) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("show :: partial"));
    showProcedure().accept(id, mav, params);
    return mav;
  }

  @GetMapping(path = "/new", consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView newAjax(ModelAndView mav,
      @RequestParam MultiValueMap<String, String> params) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("new :: partial"));
    newProcedure().accept(mav, params);
    return mav;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView createAjax(ModelAndView mav,
      @RequestParam MultiValueMap<String, String> params, @RequestBody D data) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("show :: partial"));
    createProcedure().accept(mav, params, data);
    return mav;
  }

  @GetMapping(path = "/{id}/edit", consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView editAjax(@PathVariable ID id, ModelAndView mav,
      @RequestParam MultiValueMap<String, String> params) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("edit :: partial"));
    editProcedure().accept(id, mav, params);
    return mav;
  }

  @RequestMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
      method = {RequestMethod.PUT, RequestMethod.PATCH})
  default ModelAndView updateAjax(@PathVariable ID id, ModelAndView mav,
      @RequestParam MultiValueMap<String, String> params, @RequestBody D data) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("show :: partial"));
    updateProcedure().accept(id, mav, params, data);
    return mav;
  }

  @DeleteMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView deleteAjax(@PathVariable ID id, ModelAndView mav,
      @RequestParam MultiValueMap<String, String> params) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("index :: partial"));
    deleteProcedure().accept(id, mav, params);
    return mav;
  }

}
