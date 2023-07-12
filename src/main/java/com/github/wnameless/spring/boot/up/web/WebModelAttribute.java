package com.github.wnameless.spring.boot.up.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Data;

@Component("webModelAttribute")
@Data
public class WebModelAttribute {

  public static String ROUTE;
  public static String TEMPLATE;
  public static String AJAX_TARGET;
  public static String EMBEDDED_TARGET;
  public static String BACK_TARGET;
  public static String MESSAGES;
  public static String PAGE;
  public static String ITEM;
  public static String ITEM_CLASS;
  public static String ITEMS;
  public static String QUERY_CONFIG;
  public static String USER;

  @Value("${spring.boot.up.web.model.attribute.route:route}")
  public void setRoute(String route) {
    WebModelAttribute.ROUTE = route;
  }


  @Value("${spring.boot.up.web.model.attribute.template:template}")
  public void setTemplate(String template) {
    WebModelAttribute.TEMPLATE = template;
  }

  @Value("${spring.boot.up.web.model.attribute.ajax-target:ajaxTarget}")
  public void setAjaxTarget(String ajaxTarget) {
    WebModelAttribute.AJAX_TARGET = ajaxTarget;
  }

  @Value("${spring.boot.up.web.model.attribute.embedded-target:embeddedTarget}")
  public void setEmbeddedTarget(String embeddedTarget) {
    WebModelAttribute.EMBEDDED_TARGET = embeddedTarget;
  }

  @Value("${spring.boot.up.web.model.attribute.back-target:backTarget}")
  public void setBackTarget(String backTarget) {
    WebModelAttribute.BACK_TARGET = backTarget;
  }

  @Value("${spring.boot.up.web.model.attribute.messages:messages}")
  public void setModelAttrMessages(String messages) {
    WebModelAttribute.MESSAGES = messages;
  }

  @Value("${spring.boot.up.web.model.attribute.page:page}")
  public void setModelAttrPage(String page) {
    WebModelAttribute.PAGE = page;
  }

  @Value("${spring.boot.up.web.model.attribute.item:item}")
  public void setModelAttrItem(String item) {
    WebModelAttribute.ITEM = item;
  }

  @Value("${spring.boot.up.web.model.attribute.item-class:itemClass}")
  public void setModelAttrItemClass(String itemClass) {
    WebModelAttribute.ITEM_CLASS = itemClass;
  }

  @Value("${spring.boot.up.web.model.attribute.items:items}")
  public void setModelAttrItems(String items) {
    WebModelAttribute.ITEMS = items;
  }

  @Value("${spring.boot.up.web.model.attribute.query-config:queryConfig}")
  public void setModelAttQueryConfig(String queryConfig) {
    WebModelAttribute.QUERY_CONFIG = queryConfig;
  }

  @Value("${spring.boot.up.web.model.attribute.user:user}")
  public void setModelAttUser(String user) {
    WebModelAttribute.USER = user;
  }

}
