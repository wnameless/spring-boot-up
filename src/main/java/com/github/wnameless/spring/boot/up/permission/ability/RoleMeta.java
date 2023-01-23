package com.github.wnameless.spring.boot.up.permission.ability;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RoleMetadata.class)
public @interface RoleMeta {

  String key();

  String[] values() default {};

}
