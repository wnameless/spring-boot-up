package com.github.wnameless.spring.boot.up.web;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import jakarta.servlet.ServletContext;
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

  /**
   * Returns the first occurance in web request parameters by given parameter name.
   * 
   * @param paramName name of the request parameter
   * @return the first occurance in request parameters, may be null
   */
  public static String getParam(String paramName) {
    return SpringBootUp.getBean(SpringBootUpControllerAdvice.class).getParam(paramName);
  }

  /**
   * Finds the first occurance in web request parameters by given parameter name.
   * 
   * @param paramName name of the request parameter
   * @return an {@link Optional} of the first occurance in request parameters
   */
  public static Optional<String> findParam(String paramName) {
    return SpringBootUp.getBean(SpringBootUpControllerAdvice.class).findParam(paramName);
  }

  public static String getBaseUrl(HttpServletRequest request) {
    String requestUrl = request.getRequestURL().toString();
    URL url;
    try {
      url = new URI(requestUrl).toURL();
    } catch (MalformedURLException | URISyntaxException e) {
      return null;
    }

    String protocol = url.getProtocol();
    String host = url.getHost();
    int port = url.getPort();

    String homeUrl = protocol + "://" + host + (port != -1 ? ":" + port : "");
    var servletContext = SpringBootUp.getBean(ServletContext.class);
    return homeUrl + servletContext.getContextPath();
  }

}
