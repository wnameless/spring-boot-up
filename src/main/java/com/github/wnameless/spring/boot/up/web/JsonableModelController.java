/*
 *
 * Copyright 2020 Wei-Ming Wu
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package com.github.wnameless.spring.boot.up.web;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import com.github.wnameless.spring.boot.up.ApplicationContextProvider;

public interface JsonableModelController {

  @ModelAttribute
  default void setModelFormat(Model model, @RequestParam MultiValueMap<String, List<String>> params,
      @Value("${spring.web.common.model.format.key:format}") String formatKey) {
    if (params.get(formatKey) != null) {
      model.addAttribute(formatKey, params.get(formatKey).get(0));
    }
  }

  default ModelAndView jsonableMAV(Model model, String viewName) {
    String format = ApplicationContextProvider.getApplicationContext().getEnvironment()
        .getProperty("spring.web.common.model.format.key", "format");

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
