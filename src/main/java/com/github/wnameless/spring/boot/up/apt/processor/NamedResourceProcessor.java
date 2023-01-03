package com.github.wnameless.spring.boot.up.apt.processor;

import java.io.IOException;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import org.atteo.evo.inflector.English;
import com.github.wnameless.spring.boot.up.apt.processor.NamedResource.NameKey;
import com.github.wnameless.spring.boot.up.apt.processor.NamedResource.NameKeyValue;
import com.github.wnameless.spring.boot.up.apt.processor.NamedResource.NameType;
import com.google.auto.service.AutoService;
import com.google.common.base.CaseFormat;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import jakarta.inject.Named;

@SupportedAnnotationTypes("com.github.wnameless.spring.boot.up.apt.processor.NamedResource")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class NamedResourceProcessor extends AbstractProcessor {

  private String singularName;
  private String pluralName;

  private String upperCamelSingular;
  private String lowerCamelSingular;
  private String upperUnderscoreSingular;
  private String lowerUnderscoreSingular;
  private String lowerHyphenSingular;

  private String upperCamelPlural;
  private String lowerCamelPlural;
  private String upperUnderscorePlural;
  private String lowerUnderscorePlural;
  private String lowerHyphenPlural;

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (annotations.isEmpty()) return false;

    for (Element element : roundEnv.getElementsAnnotatedWith(NamedResource.class)) {
      String className = ((TypeElement) element).getQualifiedName().toString();
      String classSimpleName = element.getSimpleName().toString();
      String packageName = null;
      int lastDot = className.lastIndexOf('.');
      if (lastDot > 0) {
        packageName = className.substring(0, lastDot);
      }

      NamedResource nr = element.getAnnotation(NamedResource.class);
      singularName = nr.singular().value().trim();
      if ("".equals(singularName)) {
        singularName = classSimpleName;
      }
      pluralName = nr.plural().value().trim();
      if ("".equals(pluralName)) {
        pluralName = English.plural(singularName);
      }
      upperCamelSingular = CaseFormat.valueOf(nr.singular().type().toString())
          .to(CaseFormat.UPPER_CAMEL, singularName);
      lowerCamelSingular = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, upperCamelSingular);
      upperUnderscoreSingular =
          CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, upperCamelSingular);
      lowerUnderscoreSingular =
          CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, upperCamelSingular);
      lowerHyphenSingular = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, upperCamelSingular);

      upperCamelPlural =
          CaseFormat.valueOf(nr.plural().type().toString()).to(CaseFormat.UPPER_CAMEL, pluralName);
      lowerCamelPlural = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, upperCamelPlural);
      upperUnderscorePlural =
          CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, upperCamelPlural);
      lowerUnderscorePlural =
          CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, upperCamelPlural);
      lowerHyphenPlural = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, upperCamelPlural);

      NameKey resourceNameKey = nr.resourceNameKey();
      NameKey resourcesNameKey = nr.resourcesNameKey();
      NameKey resourcePathNameKey = nr.resourcePathNameKey();

      TypeSpec.Builder builder =
          TypeSpec.classBuilder(nr.classNamePrefix() + classSimpleName + nr.classNameSuffix())
              .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

      // Apply jakarta.inject
      if (nr.injectable()) {
        builder.addAnnotation(Named.class);
        builder.addSuperinterface(INamedResource.class);
      }

      // RESUORCE
      this.nameKeyBuilder(builder, resourceNameKey);
      // RESUORCES
      this.nameKeyBuilder(builder, resourcesNameKey);
      // RESUORCE_PATH
      this.nameKeyBuilder(builder, resourcePathNameKey);

      // SINGULAR = singularName
      builder
          .addField(FieldSpec.builder(String.class, "SINGULAR")
              .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
              .initializer("$S", singularName).build())
          // PLURAL = singularName
          .addField(FieldSpec.builder(String.class, "PLURAL")
              .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
              .initializer("$S", pluralName).build())
          // CLASS_SIMPLE_NAME =
          // classSimpleName
          .addField(FieldSpec.builder(String.class, "CLASS_SIMPLE_NAME")
              .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
              .initializer("$S", classSimpleName).build())
          // CLASS_NAME = className
          .addField(FieldSpec.builder(String.class, "CLASS_NAME")
              .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
              .initializer("$S", className).build())
          // PACKAGE_NAME = packageName
          .addField(FieldSpec.builder(String.class, "PACKAGE_NAME")
              .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
              .initializer("$S", packageName).build())

          // UPPER_CAMEL_SINGULAR
          .addField(FieldSpec.builder(String.class, "UPPER_CAMEL_SINGULAR")
              .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
              .initializer("$S", upperCamelSingular).build())
          // LOWER_CAMEL_SINGULAR
          .addField(FieldSpec.builder(String.class, "LOWER_CAMEL_SINGULAR")
              .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
              .initializer("$S", lowerCamelSingular).build())
          // UPPER_UNDERSCORE_SINGULAR
          .addField(FieldSpec.builder(String.class, "UPPER_UNDERSCORE_SINGULAR")
              .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
              .initializer("$S", upperUnderscoreSingular).build())
          // LOWER_UNDERSCORE_SINGULAR
          .addField(FieldSpec.builder(String.class, "LOWER_UNDERSCORE_SINGULAR")
              .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
              .initializer("$S", lowerUnderscoreSingular).build())
          // LOWER_HYPHEN_SINGULAR
          .addField(FieldSpec.builder(String.class, "LOWER_HYPHEN_SINGULAR")
              .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
              .initializer("$S", lowerHyphenSingular).build())

          // UPPER_CAMEL_PLURAL
          .addField(FieldSpec.builder(String.class, "UPPER_CAMEL_PLURAL")
              .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
              .initializer("$S", upperCamelPlural).build())
          // LOWER_CAMEL_PLURAL
          .addField(FieldSpec.builder(String.class, "LOWER_CAMEL_PLURAL")
              .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
              .initializer("$S", lowerCamelPlural).build())
          // UPPER_UNDERSCORE_PLURAL
          .addField(FieldSpec.builder(String.class, "UPPER_UNDERSCORE_PLURAL")
              .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
              .initializer("$S", upperUnderscorePlural).build())
          // LOWER_UNDERSCORE_PLURAL
          .addField(FieldSpec.builder(String.class, "LOWER_UNDERSCORE_PLURAL")
              .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
              .initializer("$S", lowerUnderscorePlural).build())
          // LOWER_HYPHEN_PLURAL
          .addField(FieldSpec.builder(String.class, "LOWER_HYPHEN_PLURAL")
              .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
              .initializer("$S", lowerHyphenPlural).build());

      for (NameType nameType : nr.literalSingularConstants()) {
        switch (nameType) {
          case LOWER_CAMEL:
            builder.addField(FieldSpec.builder(String.class, lowerCamelSingular)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", lowerCamelSingular).build());
            break;
          case LOWER_UNDERSCORE:
            builder.addField(FieldSpec.builder(String.class, lowerUnderscoreSingular)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", lowerUnderscoreSingular).build());
            break;
          case UPPER_CAMEL:
            builder.addField(FieldSpec.builder(String.class, upperCamelSingular)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", upperCamelSingular).build());
            break;
          case UPPER_UNDERSCORE:
            builder.addField(FieldSpec.builder(String.class, upperUnderscoreSingular)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", upperUnderscoreSingular).build());
            break;
          default:
            break;
        }
      }

      for (NameType nameType : nr.literalPluralConstants()) {
        switch (nameType) {
          case LOWER_CAMEL:
            builder.addField(FieldSpec.builder(String.class, lowerCamelPlural)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", lowerCamelPlural).build());
            break;
          case LOWER_UNDERSCORE:
            builder.addField(FieldSpec.builder(String.class, lowerUnderscorePlural)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", lowerUnderscorePlural).build());
            break;
          case UPPER_CAMEL:
            builder.addField(FieldSpec.builder(String.class, upperCamelPlural)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", upperCamelPlural).build());
            break;
          case UPPER_UNDERSCORE:
            builder.addField(FieldSpec.builder(String.class, upperUnderscorePlural)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", upperUnderscorePlural).build());
            break;
          default:
            break;
        }
      }

      for (NameKey nameKey : nr.nameKeys()) {
        nameKeyBuilder(builder, nameKey);
      }

      for (NameKeyValue nameKeyValue : nr.nameKeyValues()) {
        builder.addField(FieldSpec.builder(String.class, nameKeyValue.key())
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            .initializer("$S", nameKeyValue.value()).build());
      }

      // Writes to file
      TypeSpec namedResourceType = builder.build();
      JavaFile javaFile = JavaFile.builder(packageName, namedResourceType).build();
      // javaFile.toJavaFileObject().delete();
      try {
        javaFile.writeTo(processingEnv.getFiler());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    return !roundEnv.getElementsAnnotatedWith(NamedResource.class).isEmpty();
  }

  private void nameKeyBuilder(TypeSpec.Builder builder, NameKey nameKey) {
    switch (nameKey.type()) {
      case LOWER_CAMEL:
        builder.addField(FieldSpec.builder(String.class, nameKey.key())
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            .initializer("$S", nameKey.prefix()
                + (nameKey.plural() ? lowerCamelPlural : lowerCamelSingular) + nameKey.suffix())
            .build());
        break;
      case LOWER_HYPHEN:
        builder.addField(FieldSpec.builder(String.class, nameKey.key())
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            .initializer("$S", nameKey.prefix()
                + (nameKey.plural() ? lowerHyphenPlural : lowerHyphenSingular) + nameKey.suffix())
            .build());
        break;
      case LOWER_UNDERSCORE:
        builder.addField(FieldSpec.builder(String.class, nameKey.key())
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            .initializer("$S",
                nameKey.prefix()
                    + (nameKey.plural() ? lowerUnderscorePlural : lowerUnderscoreSingular)
                    + nameKey.suffix())
            .build());
        break;
      case UPPER_CAMEL:
        builder.addField(FieldSpec.builder(String.class, nameKey.key())
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            .initializer("$S", nameKey.prefix()
                + (nameKey.plural() ? upperCamelPlural : upperCamelSingular) + nameKey.suffix())
            .build());
        break;
      case UPPER_UNDERSCORE:
        builder.addField(FieldSpec.builder(String.class, nameKey.key())
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            .initializer("$S",
                nameKey.prefix()
                    + (nameKey.plural() ? upperUnderscorePlural : upperUnderscoreSingular)
                    + nameKey.suffix())
            .build());
        break;
      default:
        break;
    }
  }

}
