package com.github.wnameless.spring.boot.up.jsf;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

public final class JsfConfig {

  private JsfConfig() {}

  private static String templatePath = "jsf-templates";
  private static String defaultBranchName = "main";
  private static ModelMapper modelMapper;

  public static String getTemplatePath() {
    return templatePath;
  }

  public static void setTemplatePath(String templatePath) {
    JsfConfig.templatePath = templatePath;
  }

  public static String getDefaultBranchName() {
    return defaultBranchName;
  }

  public static void setDefaultBranchName(String defaultBranchName) {
    JsfConfig.defaultBranchName = defaultBranchName;
  }

  public static ModelMapper getModelMapper() {
    if (modelMapper != null) return modelMapper;

    modelMapper = new ModelMapper();
    modelMapper.getConfiguration() //
        .setSkipNullEnabled(true) //
        .setCollectionsMergeEnabled(false) //
        .setPreferNestedProperties(false) //
        .setMatchingStrategy(MatchingStrategies.STRICT);
    return modelMapper;
  }

  public static void setModelMapper(ModelMapper modelMapper) {
    JsfConfig.modelMapper = modelMapper;
  }

}
