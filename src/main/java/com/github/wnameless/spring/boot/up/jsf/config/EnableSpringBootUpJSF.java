package com.github.wnameless.spring.boot.up.jsf.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import com.github.wnameless.spring.boot.up.jsf.service.JsfPOJOService;

@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({SprinBootUpJSFRegistrar.class, JsfPOJOService.class})
public @interface EnableSpringBootUpJSF {}
