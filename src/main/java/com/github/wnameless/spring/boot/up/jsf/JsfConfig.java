package com.github.wnameless.spring.boot.up.jsf;

public final class JsfConfig {

  private JsfConfig() {}

  private static String defaultBranchName = "main";

  public static String getDefaultBranchName() {
    return defaultBranchName;
  }

  public static void setDefaultBranchName(String defaultBranchName) {
    JsfConfig.defaultBranchName = defaultBranchName;
  }

}
