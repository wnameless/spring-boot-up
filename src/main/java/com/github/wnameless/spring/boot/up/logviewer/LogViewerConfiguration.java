package com.github.wnameless.spring.boot.up.logviewer;

import java.util.function.BooleanSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring @Configuration class for the real-time log viewer. This class is imported by
 * the @EnableLogViewer annotation. It registers the LogController, sets up the view controller, and
 * creates a BooleanSupplier bean based on the @EnableLogViewer configuration, which is then used by
 * Spring Security for access control.
 */
@Configuration
public class LogViewerConfiguration implements WebMvcConfigurer, ImportAware {

  private Class<? extends BooleanSupplier> accessConditionClass;

  /**
   * Implementation of ImportAware to capture metadata from the @EnableLogViewer annotation. This
   * allows us to read the 'accessCondition' attribute.
   */
  @Override
  public void setImportMetadata(AnnotationMetadata importMetadata) {
    AnnotationAttributes attributes = AnnotationAttributes
        .fromMap(importMetadata.getAnnotationAttributes(EnableLogViewer.class.getName()));
    if (attributes != null) {
      this.accessConditionClass = attributes.getClass("accessCondition");
    }
  }

  /**
   * Defines the LogController as a Spring Bean. This bean is always created. Spring will
   * automatically inject the 'logViewerAccessSupplier' bean into the constructor of LogController.
   * 
   * @param logViewerAccessSupplier The BooleanSupplier bean for access control, automatically
   *        injected by Spring.
   * @return An instance of LogController.
   */
  @Bean
  public LogController logController(BooleanSupplier logViewerAccessSupplier) {
    // Now, we pass the injected BooleanSupplier to the LogController's constructor.
    return new LogController(logViewerAccessSupplier);
  }

  /**
   * Configures a view controller to directly map /log-viewer to the Thymeleaf template. This
   * mapping is always registered.
   * 
   * @param registry The ViewControllerRegistry to add view controllers to.
   */
  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/log-viewer").setViewName("sbu/log-viewer/log-viewer");
  }

  /**
   * Creates a BooleanSupplier bean based on the 'accessCondition' specified in the @EnableLogViewer
   * annotation. This bean will be used by LogController for authorization decisions. The bean name
   * is explicitly set to 'logViewerAccessSupplier'.
   * 
   * @return An instance of the specified BooleanSupplier.
   * @throws Exception If the BooleanSupplier class cannot be instantiated.
   */
  @Bean(name = "logViewerAccessSupplier")
  public BooleanSupplier logViewerAccessSupplier() throws Exception {
    if (accessConditionClass == null) {
      // Fallback to default if somehow not set (shouldn't happen with @EnableLogViewer default)
      return new AlwaysTrueBooleanSupplier();
    }
    // Instantiate the BooleanSupplier using its no-arg constructor
    return accessConditionClass.getDeclaredConstructor().newInstance();
  }

}
