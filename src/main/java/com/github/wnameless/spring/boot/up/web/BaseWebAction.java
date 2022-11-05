package com.github.wnameless.spring.boot.up.web;

import org.springframework.web.servlet.ModelAndView;

public interface BaseWebAction<D> {

  void indexAction(ModelAndView mav);

  void showAction(ModelAndView mav);

  void newAction(ModelAndView mav);

  void createAction(ModelAndView mav, D data);

  void editAction(ModelAndView mav);

  void updateAction(ModelAndView mav, D data);

  void deleteAction(ModelAndView mav);

}
