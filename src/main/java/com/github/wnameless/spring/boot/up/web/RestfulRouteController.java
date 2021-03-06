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

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

public interface RestfulRouteController<ID> {

  RestfulRoute<ID> getRoute();

  default RestfulRoute<ID> getTemplate() {
    return getRoute().getTemplateRoute();
  }

  default String getRouteKey() {
    return "route";
  }

  default String getTemplateKey() {
    return "template";
  }

  @ModelAttribute
  default void setRoute(Model model) {
    model.addAttribute(getRouteKey(), getRoute());
  }

  @ModelAttribute
  default void setTemplate(Model model) {
    model.addAttribute(getTemplateKey(), getTemplate());
  }

}
