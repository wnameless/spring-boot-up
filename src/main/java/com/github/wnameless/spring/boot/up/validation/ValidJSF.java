package com.github.wnameless.spring.boot.up.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = ValidJSFValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidJSF {

  String message() default "This JsonSchemaForm is invalid according to its schema";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
