package com.github.wnameless.spring.boot.up.web;

import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ModelHelper {

  public void forwrdAttributes(Model model, MultiValueMap<String, String> params) {
    for (String name : params.keySet()) {
      if (name.startsWith(WebModelAttributes.FORWARDABLE_ATTRIBUTE_PREFIX)) {
        model.addAttribute(name, params.getFirst(name));
      }
    }
  }

}
