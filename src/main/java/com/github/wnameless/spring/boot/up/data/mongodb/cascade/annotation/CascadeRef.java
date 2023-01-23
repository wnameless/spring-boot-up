package com.github.wnameless.spring.boot.up.data.mongodb.cascade.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.github.wnameless.spring.boot.up.data.mongodb.cascade.CascadeType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CascadeRef {

  CascadeType[] value() default {CascadeType.ALL};

}
