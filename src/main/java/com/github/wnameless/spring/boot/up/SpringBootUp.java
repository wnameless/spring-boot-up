/*
 *
 * Copyright 2021 Wei-Ming Wu
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
package com.github.wnameless.spring.boot.up;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.github.wnameless.spring.boot.up.web.WebUiModelHolder;

public final class SpringBootUp {

  private SpringBootUp() {}

  public static void cacheWebUiModel(HttpServletRequest req, Model model) {
    WebUiModelHolder webUiModelHolder = getBean(WebUiModelHolder.class);
    webUiModelHolder.cacheModel(req, model);
  }

  public static Model retrieveWebUiModel() {
    WebUiModelHolder webUiModelHolder = getBean(WebUiModelHolder.class);
    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
            .getRequest();

    return webUiModelHolder.retrieveModel(request);
  }

  public static ApplicationContext applicationContext() {
    return ApplicationContextProvider.getApplicationContext();
  }

  public static <T> T getBean(Class<T> requiredType) {
    return ApplicationContextProvider.getApplicationContext()
        .getBean(requiredType);
  }

  public static Object getBean(String name) {
    return ApplicationContextProvider.getApplicationContext().getBean(name);
  }

  public static <T> Map<String, T> getBeansOfType(Class<T> type) {
    return ApplicationContextProvider.getApplicationContext()
        .getBeansOfType(type);
  }

  public static void autowireBean(Object instance) {
    ApplicationContextProvider.getApplicationContext()
        .getAutowireCapableBeanFactory().autowireBean(instance);
  }

}
