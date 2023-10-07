package com.github.wnameless.spring.boot.up.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.ModelAndView;

@RequestScope
@ControllerAdvice
public class ModelAndViewControllerAdvice {

  private ModelAndView mav;

  @ModelAttribute
  public void initModelAndView(ModelAndView mav) {
    this.mav = mav;
  }

  public ModelAndView getModelAndView() {
    return mav;
  }

}
