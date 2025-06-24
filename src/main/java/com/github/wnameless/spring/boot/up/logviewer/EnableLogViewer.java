package com.github.wnameless.spring.boot.up.logviewer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.BooleanSupplier;
import org.springframework.context.annotation.Import;

/**
 * Enables the real-time log viewer functionality in a Spring Boot application. When this annotation
 * is applied, it registers the LogController and provides a mechanism to control access to the
 * /log-viewer endpoint using a custom BooleanSupplier.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LogViewerConfiguration.class) // Imports the core configuration for the viewer
public @interface EnableLogViewer {

  /**
   * Specifies a BooleanSupplier class whose getAsBoolean() method will be used to determine if the
   * current user (or context) is permitted to access the /log-viewer endpoint.
   *
   * The provided class must have a no-argument constructor. The default is
   * AlwaysTrueBooleanSupplier.class, which allows access unconditionally.
   *
   * Note: This condition is applied at the authorization layer via Spring Security, not for
   * conditional bean registration.
   *
   * @return The Class of the BooleanSupplier to use for access control.
   */
  Class<? extends BooleanSupplier> accessCondition() default AlwaysTrueBooleanSupplier.class;

}
