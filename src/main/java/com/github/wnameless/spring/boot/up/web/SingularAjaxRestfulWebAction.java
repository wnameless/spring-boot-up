package com.github.wnameless.spring.boot.up.web;

import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

public interface SingularAjaxRestfulWebAction<D, ID>
    extends SingularBaseWebAction<D>, RestfulRouteProvider<Void> {

  @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView showAjax(ModelAndView mav,
      @RequestParam MultiValueMap<String, String> params) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("show :: partial"));
    showProcedure().accept(mav, params);
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

  @GetMapping(path = "edit", consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView editAjax(ModelAndView mav,
      @RequestParam MultiValueMap<String, String> params) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("edit :: partial"));
    editProcedure().accept(mav, params);
    return mav;
  }

  @RequestMapping(method = {RequestMethod.PUT, RequestMethod.PATCH},
      consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView updateAjax(ModelAndView mav,
      @RequestParam MultiValueMap<String, String> params, @RequestBody D data) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("edit :: partial"));
    updateProcedure().accept(mav, params, data);
    return mav;
  }

  @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView deleteAjax(ModelAndView mav,
      @RequestParam MultiValueMap<String, String> params) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("show :: partial"));
    deleteProcedure().accept(mav, params);
    return mav;
  }

}
