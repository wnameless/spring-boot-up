package com.github.wnameless.spring.boot.up.tagging;

import com.github.wnameless.spring.boot.up.SpringBootUp;

public class TaggingI18nHelper {

  public static String getTaggingTitle() {
    return SpringBootUp.getMessage("sbu.tagging.title", null, "註記標籤");
  }

  public static String getLabelListName() {
    return SpringBootUp.getMessage("sbu.tagging.labelList.title", null, "公用標籤");
  }

  public static String getUserLabelListName() {
    return SpringBootUp.getMessage("sbu.tagging.userLabelList.title", null, "私人標籤");
  }

  public static String getSystemLabelListName() {
    return SpringBootUp.getMessage("sbu.tagging.systemLabelList.title", null, "系統標籤");
  }

}
