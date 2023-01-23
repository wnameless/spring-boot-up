package com.github.wnameless.spring.boot.up;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 
 * {@link ApplicationContextProvider} is made for SpringBootUp library to store the Spring
 * {@link ApplicationContext} of current running application.
 * 
 * @author Wei-Ming Wu
 * 
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public final class ApplicationContextProvider implements ApplicationContextAware {

  private static final class AplicationContextHolder {

    private static final InnerContextResource RESOURCE = new InnerContextResource();

    private AplicationContextHolder() {}

  }

  private static final class InnerContextResource {

    private ApplicationContext context;

    private InnerContextResource() {}

    private void setContext(ApplicationContext context) {
      this.context = context;
    }

  }

  public static ApplicationContext getApplicationContext() {
    return AplicationContextHolder.RESOURCE.context;
  }

  @Override
  public void setApplicationContext(ApplicationContext ac) {
    AplicationContextHolder.RESOURCE.setContext(ac);
  }

}
