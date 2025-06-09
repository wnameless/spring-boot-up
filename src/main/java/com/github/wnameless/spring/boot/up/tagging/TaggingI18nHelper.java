package com.github.wnameless.spring.boot.up.tagging;

import com.github.wnameless.spring.boot.up.SpringBootUp;

public class TaggingI18nHelper {

  public static String getTaggingTitle() {
    return SpringBootUp.getMessage("sbu.tagging.panel.tagging", null, "Tagging Labels");
  }

  public static String getLabelListName() {
    return SpringBootUp.getMessage("sbu.tagging.panel.labelList", null, "Public Labels");
  }

  public static String getUserLabelListName() {
    return SpringBootUp.getMessage("sbu.tagging.panel.userLabelList", null, "Private Labels");
  }

  public static String getSystemLabelListName() {
    return SpringBootUp.getMessage("sbu.tagging.panel.systemLabelList", null, "System Labels");
  }

}
