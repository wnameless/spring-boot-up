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
import com.github.wnameless.spring.boot.up.apt.processor.NamedResource.Constant;
import com.github.wnameless.spring.boot.up.apt.processor.NamedResource.InferredConstant;
import com.github.wnameless.spring.boot.up.apt.processor.NamedResource.NamingType;
import com.google.auto.service.AutoService;
import com.google.common.base.CaseFormat;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import jakarta.inject.Named;
import net.sf.rubycollect4j.Ruby;

@SupportedAnnotationTypes("com.github.wnameless.spring.boot.up.apt.processor.NamedResource")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class NamedResourceAutoProcessor extends AbstractProcessor {

  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(NamedResourceAutoProcessor.class);

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
      log.info("NamedResource: constants generating for class: {}", className);;
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

      TypeSpec.Builder builder =
          TypeSpec.classBuilder(nr.classNamePrefix() + classSimpleName + nr.classNameSuffix())
              .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

      // Apply jakarta.inject
      if (nr.injectable()) {
        builder.addAnnotation(Named.class);
        builder.addSuperinterface(INamedResource.class);
      }

      InferredConstant resource = nr.resource();
      InferredConstant resources = nr.resources();
      InferredConstant resourcePath = nr.resourcePath();
      // RESUORCE
      this.nameKeyBuilder(builder, resource);
      // RESUORCES
      this.nameKeyBuilder(builder, resources);
      // RESUORCE_PATH
      this.nameKeyBuilder(builder, resourcePath);

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

      for (NamingType nameType : nr.literalSingularConstants()) {
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

      for (NamingType nameType : nr.literalPluralConstants()) {
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

      for (InferredConstant nameKey : nr.inferredConstants()) {
        nameKeyBuilder(builder, nameKey);
      }

      for (Constant constant : nr.constants()) {
        builder.addField(constant(constant.name(), constant.value()));
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

  private void nameKeyBuilder(TypeSpec.Builder builder, InferredConstant constant) {
    switch (constant.type()) {
      case UPPER_UNDERSCORE:
        builder.addField(constant(constant.name(),
            constant.prefix()
                + (constant.plural() ? upperUnderscorePlural : upperUnderscoreSingular)
                + constant.suffix()));
        break;
      case LOWER_UNDERSCORE:
        builder.addField(constant(constant.name(),
            constant.prefix()
                + (constant.plural() ? lowerUnderscorePlural : lowerUnderscoreSingular)
                + constant.suffix()));
        break;
      case UPPER_CAMEL:
        builder.addField(constant(constant.name(), constant.prefix()
            + (constant.plural() ? upperCamelPlural : upperCamelSingular) + constant.suffix()));
        break;
      case LOWER_CAMEL:
        builder.addField(constant(constant.name(), constant.prefix()
            + (constant.plural() ? lowerCamelPlural : lowerCamelSingular) + constant.suffix()));
        break;
      case LOWER_HYPHEN:
        builder.addField(constant(constant.name(), constant.prefix()
            + (constant.plural() ? lowerHyphenPlural : lowerHyphenSingular) + constant.suffix()));
        break;
    }
  }

  private FieldSpec constant(String name, String value) {
    return FieldSpec.builder(String.class, name)
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL).initializer("$S", value)
        .build();
  }

}
