package com.github.wnameless.spring.boot.up.web;

import java.util.Optional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class SpringBootUpWeb {

  private SpringBootUpWeb() {}

  /**
   * Finds the {@link HttpServletRequest} bound to the thread.
   * 
   * @return an {@link Optional} of {@link HttpServletRequest}
   */
  public static Optional<HttpServletRequest> findHttpServletRequest() {
    return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
        .filter(ServletRequestAttributes.class::isInstance)
        .map(ServletRequestAttributes.class::cast).map(ServletRequestAttributes::getRequest);
  }

  /**
   * Finds the {@link HttpServletResponse} bound to the thread.
   * 
   * @return an {@link Optional} of {@link HttpServletResponse}
   */
  public static Optional<HttpServletResponse> findHttpServletResponse() {
    return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
        .filter(ServletRequestAttributes.class::isInstance)
        .map(ServletRequestAttributes.class::cast).map(ServletRequestAttributes::getResponse);
  }

}
