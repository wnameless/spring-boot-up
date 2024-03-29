package com.github.wnameless.spring.boot.up.web;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import com.github.wnameless.spring.boot.up.SpringBootUp;

public interface JsonableModelController {

  @ModelAttribute
  default void setModelFormat(Model model, @RequestParam MultiValueMap<String, List<String>> params,
      @Value("${sbu.web.common.model.format.key:format}") String formatKey) {
    if (params.get(formatKey) != null) {
      model.addAttribute(formatKey, params.get(formatKey).get(0));
    }
  }

  default ModelAndView jsonableMAV(Model model, String viewName) {
    String format =
        SpringBootUp.environment().getProperty("sbu.web.common.model.format.key", "format");

    ModelAndView mav = new ModelAndView();

    if ("json".equals(model.getAttribute(format))) {
      MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
      model.asMap().remove(format);
      mav.setView(jsonView);
    } else {
      mav.setViewName(viewName);
    }

    return mav;
  }

}
