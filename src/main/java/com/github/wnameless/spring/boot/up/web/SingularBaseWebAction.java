package com.github.wnameless.spring.boot.up.web;

import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.ModelAndView;

public interface SingularBaseWebAction<D> extends BaseWebAction<D> {

  default void indexAction(ModelAndView mav, MultiValueMap<String, String> params) {}

}
