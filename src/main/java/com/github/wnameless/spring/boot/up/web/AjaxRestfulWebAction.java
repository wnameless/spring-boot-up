package com.github.wnameless.spring.boot.up.web;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

public interface AjaxRestfulWebAction<D, ID> extends BaseWebAction<D>, RestfulRouteProvider<ID> {

  @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView indexAjax(ModelAndView mav) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("index :: partial"));
    indexProcedure().accept(mav);
    return mav;
  }

  @GetMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView showAjax(ModelAndView mav) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("show :: partial"));
    showProcedure().accept(mav);
    return mav;
  }

  @GetMapping(path = "/new", consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView newAjax(ModelAndView mav) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("new :: partial"));
    newProcedure().accept(mav);
    return mav;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView createAjax(ModelAndView mav, @RequestBody D data) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("show :: partial"));
    createProcedure().accept(mav, data);
    return mav;
  }

  @GetMapping(path = "/{id}/edit", consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView editAjax(ModelAndView mav) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("edit :: partial"));
    editProcedure().accept(mav);
    return mav;
  }

  @RequestMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
      method = {RequestMethod.PUT, RequestMethod.PATCH})
  default ModelAndView updateAjax(ModelAndView mav, @RequestBody D data) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("show :: partial"));
    updateProcedure().accept(mav, data);
    return mav;
  }

  @DeleteMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView deleteAjax(ModelAndView mav) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("index :: partial"));
    deleteProcedure().accept(mav);
    return mav;
  }

}
