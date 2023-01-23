package com.github.wnameless.spring.boot.up.permission.ability;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.github.wnameless.spring.boot.up.permission.resource.ResourceAccessRule;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CanCreate {

  Class<? extends ResourceAccessRule<?, ?, ?>>[] value();

}
