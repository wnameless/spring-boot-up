package com.github.wnameless.spring.boot.up.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Data;

@Component("webModelAttribute")
@Data
public class WebModelAttribute {

  public static String AJAX_TARGET_ID;
  public static String MESSAGES;
  public static String PAGE;
  public static String ITEM;
  public static String ITEMS;
  public static String QUERY_CONFIG;
  public static String USER;

  @Value("${spring.boot.up.web.model.attribute.ajax-target-id:ajaxTarget}")
  public void setAjaxTargetId(String ajaxTargetId) {
    WebModelAttribute.AJAX_TARGET_ID = ajaxTargetId;
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
