package com.github.wnameless.spring.boot.up.apt.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface NamedResource {

    boolean injectable() default false;

    NameValue singular() default @NameValue(value = "",
            type = NameType.UPPER_CAMEL);

    NameValue plural() default @NameValue(value = "",
            type = NameType.UPPER_CAMEL);

    String classNamePrefix() default "NR";

    String classNameSuffix() default "";

    NameKey resourceNameKey() default @NameKey(key = "RESOURCE", plural = false,
            type = NameType.LOWER_HYPHEN);

    NameKey resourcesNameKey() default @NameKey(key = "RESOURCES",
            plural = true, type = NameType.LOWER_HYPHEN);

    NameKey resourcePathNameKey() default @NameKey(key = "RESOURCE_PATH",
            plural = true, type = NameType.LOWER_HYPHEN, prefix = "/");

    NameType[] literalSingularConstants() default {};

    NameType[] literalPluralConstants() default {};

    NameKey[] nameKeys() default {};

    NameKeyValue[] nameKeyValues() default {};

    public enum NameType {

        UPPER_CAMEL, LOWER_CAMEL, UPPER_UNDERSCORE, LOWER_UNDERSCORE,
        LOWER_HYPHEN;

    }

    @Target(ElementType.ANNOTATION_TYPE)
    @Retention(RetentionPolicy.SOURCE)
    public @interface NameKey {

        String key();

        boolean plural();

        NameType type();

        String prefix() default "";

        String suffix() default "";

    }

    @Target(ElementType.ANNOTATION_TYPE)
    @Retention(RetentionPolicy.SOURCE)
    public @interface NameValue {

        String value();

        NameType type();

    }

    @Target(ElementType.ANNOTATION_TYPE)
    @Retention(RetentionPolicy.SOURCE)
    public @interface NameKeyValue {

        String key();

        String value();

    }

}
