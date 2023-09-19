package com.github.wnameless.spring.boot.up.web;

public class ModelAttributes {

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
