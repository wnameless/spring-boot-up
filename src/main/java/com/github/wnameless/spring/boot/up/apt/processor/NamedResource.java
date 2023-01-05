package com.github.wnameless.spring.boot.up.apt.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface NamedResource {

  boolean injectable() default false;

  String classNamePrefix() default "NR";

  String classNameSuffix() default "";

  NameValue singular() default @NameValue(value = "", type = NamingType.UPPER_CAMEL);

  NameValue plural() default @NameValue(value = "", type = NamingType.UPPER_CAMEL);

  InferredConstant resource() default @InferredConstant(name = "RESOURCE", plural = false,
      type = NamingType.LOWER_HYPHEN);

  InferredConstant resources() default @InferredConstant(name = "RESOURCES", plural = true,
      type = NamingType.LOWER_HYPHEN);

  InferredConstant resourcePath() default @InferredConstant(name = "RESOURCE_PATH", plural = true,
      type = NamingType.LOWER_HYPHEN, prefix = "/");

  NamingType[] literalSingularConstants() default {};

  NamingType[] literalPluralConstants() default {};

  InferredConstant[] inferredConstants() default {};

  Constant[] constants() default {};

  public enum NamingType {

    UPPER_CAMEL, LOWER_CAMEL, UPPER_UNDERSCORE, LOWER_UNDERSCORE, LOWER_HYPHEN;

  }

  @Target(ElementType.ANNOTATION_TYPE)
  @Retention(RetentionPolicy.SOURCE)
  public @interface NameValue {

    String value();

    NamingType type();

  }

  @Target(ElementType.ANNOTATION_TYPE)
  @Retention(RetentionPolicy.SOURCE)
  public @interface InferredConstant {

    String name();

    boolean plural();

    NamingType type();

    String prefix() default "";

    String suffix() default "";

  }

  @Target(ElementType.ANNOTATION_TYPE)
  @Retention(RetentionPolicy.SOURCE)
  public @interface Constant {

    String name();

    String value();

  }

}
