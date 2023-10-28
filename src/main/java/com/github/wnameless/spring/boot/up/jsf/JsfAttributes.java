package com.github.wnameless.spring.boot.up.jsf;

import static com.github.wnameless.spring.boot.up.web.WebModelAttributes.*;
import java.util.List;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.ModelAndView;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsfAttributes {

  public static final String BACKABLE_NAME = "backable";

  public void forwardJsfAttrs(ModelAndView mav, MultiValueMap<String, String> params) {
    List.of(AJAX_TARGET_ID_NAME, EMBEDDED_TARGET_ID_NAME, BACK_TARGET_ID_NAME, BACKABLE_NAME)
        .forEach(name -> {
          if (params.containsKey(name)) {
            mav.addObject(name, params.getFirst(name));
          }
        });
  }

}
