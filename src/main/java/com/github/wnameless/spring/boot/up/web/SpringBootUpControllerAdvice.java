package com.github.wnameless.spring.boot.up.web;

import java.util.List;
import java.util.Optional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.ModelAndView;

@RequestScope
@ControllerAdvice
public class SpringBootUpControllerAdvice {

  private ModelAndView mav;
  private MultiValueMap<String, String> params;

  @ModelAttribute
  void init(ModelAndView mav, @RequestParam MultiValueMap<String, String> params) {
    this.mav = mav;
    this.params = params;
  }

  public ModelAndView getModelAndView() {
    return mav;
  }

  public MultiValueMap<String, String> getParams() {
    return params;
  }

  public String getParam(String key) {
    return params.getFirst(key);
  }

  public Optional<String> findParam(String key) {
    return Optional.ofNullable(params.getFirst(key));
  }

  public Optional<List<String>> findAllParams(String key) {
    return Optional.ofNullable(params.get(key));
  }

}
