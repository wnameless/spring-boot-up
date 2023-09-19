package com.github.wnameless.spring.boot.up.web.modelattributes;

import com.github.wnameless.spring.boot.up.web.WebModelAttribute;

public class Attr {

  public static class Page {

    public static String name() {
      return WebModelAttribute.PAGE;
    }

  }

  public static class AjaxTargetId {

    public static String name() {
      return "ajaxTargetId";
    }

    public static String value() {
      return WebModelAttribute.AJAX_TARGET;
    }

  }

}
