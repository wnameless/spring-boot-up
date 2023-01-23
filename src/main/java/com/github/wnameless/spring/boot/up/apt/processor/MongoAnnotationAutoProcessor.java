package com.github.wnameless.spring.boot.up.apt.processor;

import java.util.Collections;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.tools.Diagnostic;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;
import com.google.auto.service.AutoService;
import com.querydsl.apt.AbstractQuerydslProcessor;
import com.querydsl.apt.Configuration;
import com.querydsl.apt.DefaultConfiguration;
import com.querydsl.core.annotations.QueryEmbeddable;
import com.querydsl.core.annotations.QueryEmbedded;
import com.querydsl.core.annotations.QueryEntities;
import com.querydsl.core.annotations.QuerySupertype;
import com.querydsl.core.annotations.QueryTransient;

@SupportedAnnotationTypes({"com.querydsl.core.annotations.*",
    "org.springframework.data.mongodb.core.mapping.*"})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class MongoAnnotationAutoProcessor extends AbstractQuerydslProcessor {

  @Override
  protected Configuration createConfiguration(@Nullable RoundEnvironment roundEnv) {

    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
        "Running " + getClass().getSimpleName());

    DefaultConfiguration configuration = new DefaultConfiguration(processingEnv, roundEnv,
        Collections.emptySet(), QueryEntities.class, Document.class, QuerySupertype.class,
        QueryEmbeddable.class, QueryEmbedded.class, QueryTransient.class);
    configuration.setUnknownAsEmbedded(true);

    return configuration;

  }

}
