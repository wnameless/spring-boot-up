package com.github.wnameless.spring.boot.up.validation.validator;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD, TYPE})
@Constraint(validatedBy = SpELAssertValidator.class)
@Repeatable(SpELAsserts.class)
public @interface SpELAssert {

  String message() default "{cz.jirutka.validator.spring.SpELAssert.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  /**
   * Validation expression in SpEL.
   *
   * @see <a href="http://static.springsource.org/spring/docs/3.0.x/reference/expressions.html">
   *      documentation of Spring Expression Language</a>
   */
  String value();

  /**
   * Perform validation only if this SpEL expression evaluates to true.
   *
   * @see <a href="http://static.springsource.org/spring/docs/3.0.x/reference/expressions.html">
   *      documentation of Spring Expression Language</a>
   */
  String applyIf() default "";

  /**
   * Classes with static methods to register as helper functions which you can call from expression
   * as #methodName(arg1, arg2, ...). This does not support overloading, only last registered method
   * of each name will be used!
   */
  Class<?>[] helpers() default {};

  /**
   * Defines several <code>@SpELAssertValidator</code> annotations on the same element.
   *
   * @see SpELAssert
   */
  @Documented
  @Retention(RUNTIME)
  @Target({METHOD, FIELD, TYPE})
  @interface List {
    SpELAssert[] value();
  }

}
