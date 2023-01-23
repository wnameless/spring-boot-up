package com.github.wnameless.spring.boot.up.permission.ability;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.github.wnameless.spring.boot.up.permission.resource.ResourceAccessRule;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Can.class)
public @interface Do {

  String action();

  Class<? extends ResourceAccessRule<?, ?, ?>>[] on();

}
