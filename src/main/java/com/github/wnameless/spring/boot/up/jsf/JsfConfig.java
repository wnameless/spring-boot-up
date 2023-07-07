package com.github.wnameless.spring.boot.up.jsf;

public final class JsfConfig {

  private JsfConfig() {}

  private static String templatePath = "jsf-templates";
  private static String defaultBranchName = "main";

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

}
