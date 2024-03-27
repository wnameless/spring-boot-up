package com.github.wnameless.spring.boot.up.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Data;

@Component(WebModelAttributes.BEAN_NAME)
@Data
public class WebModelAttributes {

  public static final String BEAN_NAME = "webModelAttributes";

  public static String ROUTE_NAME;
  public static String TEMPLATE_NAME;
  public static final String AJAX_TARGET_ID_NAME = "ajaxTargetId";
  public static String AJAX_TARGET_ID_VALUE;
  public static final String EMBEDDED_TARGET_ID_NAME = "embeddedTargetId";
  public static String EMBEDDED_TARGET_ID_VALUE;
  public static final String BACK_TARGET_ID_NAME = "backTargetId";
  public static String BACK_TARGET_ID_VALUE;
  public static final String MODEL_TARGET_ID_NAME = "modelTargetId";
  public static String MODEL_TARGET_ID_VALUE;
  public static final String MODEL_CONTENT_TARGET_ID_NAME = "modelContentTargetId";
  public static String MODEL_CONTENT_TARGET_ID_VALUE;
  public static String PAGE_NAME;
  public static String ITEM_NAME;
  public static String ITEM_CLASS_NAME;
  public static String ITEMS_NAME;
  public static String QUERY_CONFIG_NAME;
  public static String USER_NAME;
  public static String PARENT_NAME;
  public static String PARENT_CLASS_NAME;
  public static String CHILD_NAME;
  public static String CHILD_CLASS_NAME;
  public static String CHILDREN_NAME;
  public static String ALERT_NAME;
  public static String CREATION_OPTION_NAME;

  @Value("${spring.boot.up.web.model.attribute.route.name:route}")
  public void setRoute(String routeName) {
    WebModelAttributes.ROUTE_NAME = routeName;
  }

  @Value("${spring.boot.up.web.model.attribute.template.name:template}")
  public void setTemplate(String templateName) {
    WebModelAttributes.TEMPLATE_NAME = templateName;
  }

  @Value("${spring.boot.up.web.model.attribute.ajax-target-id.value:ajaxTarget}")
  public void setAjaxTarget(String ajaxTargetIdValue) {
    WebModelAttributes.AJAX_TARGET_ID_VALUE = ajaxTargetIdValue;
  }

  @Value("${spring.boot.up.web.model.attribute.embedded-target-id.value:embeddedTarget}")
  public void setEmbeddedTarget(String embeddedTargetIdValue) {
    WebModelAttributes.EMBEDDED_TARGET_ID_VALUE = embeddedTargetIdValue;
  }

  @Value("${spring.boot.up.web.model.attribute.back-target-id.value:backTarget}")
  public void setBackTarget(String backTargetIdValue) {
    WebModelAttributes.BACK_TARGET_ID_VALUE = backTargetIdValue;
  }

  @Value("${spring.boot.up.web.model.attribute.model-target-id.value:modelTarget}")
  public void setModelTarget(String modelTargetIdValue) {
    WebModelAttributes.MODEL_TARGET_ID_VALUE = modelTargetIdValue;
  }

  @Value("${spring.boot.up.web.model.attribute.model-content-target-id.value:modelContentTarget}")
  public void setModelContentTarget(String modelContentTargetIdValue) {
    WebModelAttributes.MODEL_CONTENT_TARGET_ID_VALUE = modelContentTargetIdValue;
  }

  @Value("${spring.boot.up.web.model.attribute.page.name:page}")
  public void setModelAttrPage(String pageName) {
    WebModelAttributes.PAGE_NAME = pageName;
  }

  @Value("${spring.boot.up.web.model.attribute.item.name:item}")
  public void setModelAttrItem(String itemName) {
    WebModelAttributes.ITEM_NAME = itemName;
  }

  @Value("${spring.boot.up.web.model.attribute.item-class.name:itemClass}")
  public void setModelAttrItemClass(String itemClassName) {
    WebModelAttributes.ITEM_CLASS_NAME = itemClassName;
  }

  @Value("${spring.boot.up.web.model.attribute.items.name:items}")
  public void setModelAttrItems(String itemsName) {
    WebModelAttributes.ITEMS_NAME = itemsName;
  }

  @Value("${spring.boot.up.web.model.attribute.query-config.name:queryConfig}")
  public void setModelAttrQueryConfig(String queryConfigName) {
    WebModelAttributes.QUERY_CONFIG_NAME = queryConfigName;
  }

  @Value("${spring.boot.up.web.model.attribute.user.name:user}")
  public void setModelAttrUser(String userName) {
    WebModelAttributes.USER_NAME = userName;
  }

  @Value("${spring.boot.up.web.model.attribute.parent.name:parent}")
  public void setModelAttrParent(String parentName) {
    WebModelAttributes.PARENT_NAME = parentName;
  }

  @Value("${spring.boot.up.web.model.attribute.parent-class.name:parentClass}")
  public void setModelAttrParentClass(String parentClassName) {
    WebModelAttributes.PARENT_CLASS_NAME = parentClassName;
  }

  @Value("${spring.boot.up.web.model.attribute.child.name:child}")
  public void setModelAttrChild(String childName) {
    WebModelAttributes.CHILD_NAME = childName;
  }

  @Value("${spring.boot.up.web.model.attribute.child-class.name:childClass}")
  public void setModelAttrChildClass(String childClassName) {
    WebModelAttributes.CHILD_CLASS_NAME = childClassName;
  }

  @Value("${spring.boot.up.web.model.attribute.children.name:children}")
  public void setModelAttrChildren(String childrenName) {
    WebModelAttributes.CHILDREN_NAME = childrenName;
  }

  @Value("${spring.boot.up.web.model.attribute.alert.name:alert}")
  public void setModelAttrAlert(String alertName) {
    WebModelAttributes.ALERT_NAME = alertName;
  }

  @Value("${spring.boot.up.web.model.attribute.creation-option.name:creationOption}")
  public void setModelAttrCreationOption(String creationOptionName) {
    WebModelAttributes.CREATION_OPTION_NAME = creationOptionName;
  }

}
