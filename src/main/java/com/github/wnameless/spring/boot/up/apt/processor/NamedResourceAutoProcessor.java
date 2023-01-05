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
import net.sf.rubycollect4j.Ruby;

@SupportedAnnotationTypes("com.github.wnameless.spring.boot.up.apt.processor.NamedResource")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class NamedResourceAutoProcessor extends AbstractProcessor {

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

    Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(NamedResource.class);
    for (Element element : elements) {
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

      Ruby.Hash.of( //
          "SINGULAR", singularName, //
          "PLURAL", pluralName, //
          "CLASS_SIMPLE_NAME", classSimpleName, //
          "CLASS_NAME", className, //
          "PACKAGE_NAME", packageName).forEach((k, v) -> {
            builder.addField(constant(k, v));
          });

      Ruby.Hash.of( //
          "UPPER_CAMEL_SINGULAR", upperCamelSingular, //
          "LOWER_CAMEL_SINGULAR", lowerCamelSingular, //
          "UPPER_UNDERSCORE_SINGULAR", upperUnderscoreSingular, //
          "LOWER_UNDERSCORE_SINGULAR", lowerUnderscoreSingular, //
          "LOWER_HYPHEN_SINGULAR", lowerHyphenSingular, //
          "UPPER_CAMEL_PLURAL", upperCamelPlural, //
          "LOWER_CAMEL_PLURAL", lowerCamelPlural, //
          "UPPER_UNDERSCORE_PLURAL", upperUnderscorePlural, //
          "LOWER_UNDERSCORE_PLURAL", lowerUnderscorePlural, //
          "LOWER_HYPHEN_PLURAL", lowerHyphenPlural).forEach((k, v) -> {
            builder.addField(constant(k, v));
          });

      for (NameType nameType : nr.literalSingularConstants()) {
        switch (nameType) {
          case UPPER_CAMEL:
            builder.addField(constant(upperCamelSingular, upperCamelSingular));
            break;
          case LOWER_CAMEL:
            builder.addField(constant(lowerCamelSingular, lowerCamelSingular));
            break;
          case UPPER_UNDERSCORE:
            builder.addField(constant(upperUnderscoreSingular, upperUnderscoreSingular));
            break;
          case LOWER_UNDERSCORE:
            builder.addField(constant(lowerUnderscoreSingular, lowerUnderscoreSingular));
            break;
          case LOWER_HYPHEN:
            break;
        }
      }

      for (NameType nameType : nr.literalPluralConstants()) {
        switch (nameType) {
          case UPPER_CAMEL:
            builder.addField(constant(upperCamelPlural, upperCamelPlural));
            break;
          case LOWER_CAMEL:
            builder.addField(constant(lowerCamelPlural, lowerCamelPlural));
            break;
          case UPPER_UNDERSCORE:
            builder.addField(constant(upperUnderscorePlural, upperUnderscorePlural));
            break;
          case LOWER_UNDERSCORE:
            builder.addField(constant(lowerUnderscorePlural, lowerUnderscorePlural));
            break;
          case LOWER_HYPHEN:
            break;
        }
      }

      for (NameKey nameKey : nr.nameKeys()) {
        nameKeyBuilder(builder, nameKey);
      }

      for (NameKeyValue nameKeyValue : nr.nameKeyValues()) {
        builder.addField(constant(nameKeyValue.key(), nameKeyValue.value()));
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

    return !elements.isEmpty();
  }

  private void nameKeyBuilder(TypeSpec.Builder builder, NameKey nameKey) {
    switch (nameKey.type()) {
      case UPPER_UNDERSCORE:
        builder.addField(constant(nameKey.key(),
            nameKey.prefix() + (nameKey.plural() ? upperUnderscorePlural : upperUnderscoreSingular)
                + nameKey.suffix()));
        break;
      case LOWER_UNDERSCORE:
        builder.addField(constant(nameKey.key(),
            nameKey.prefix() + (nameKey.plural() ? lowerUnderscorePlural : lowerUnderscoreSingular)
                + nameKey.suffix()));
        break;
      case UPPER_CAMEL:
        builder.addField(constant(nameKey.key(), nameKey.prefix()
            + (nameKey.plural() ? upperCamelPlural : upperCamelSingular) + nameKey.suffix()));
        break;
      case LOWER_CAMEL:
        builder.addField(constant(nameKey.key(), nameKey.prefix()
            + (nameKey.plural() ? lowerCamelPlural : lowerCamelSingular) + nameKey.suffix()));
        break;
      case LOWER_HYPHEN:
        builder.addField(constant(nameKey.key(), nameKey.prefix()
            + (nameKey.plural() ? lowerHyphenPlural : lowerHyphenSingular) + nameKey.suffix()));
        break;
    }
  }

  private FieldSpec constant(String name, String value) {
    return FieldSpec.builder(String.class, name)
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL).initializer("$S", value)
        .build();
  }

}
