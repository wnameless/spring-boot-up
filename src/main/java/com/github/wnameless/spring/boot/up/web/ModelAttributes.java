package com.github.wnameless.spring.boot.up.web;

import org.springframework.stereotype.Component;

@Component
public class ModelAttributes {

  public Class<Item> getItem() {
    return Item.class;
  }

  public static class Item {

    public static String name() {
      return getName();
    }

    public static String getName() {
      return WebModelAttributes.ITEM_NAME;
    }

  }

  public Class<ItemClass> getItemClass() {
    return ItemClass.class;
  }

  public static class ItemClass {

    public static String name() {
      return getName();
    }

    public static String getName() {
      return WebModelAttributes.ITEM_CLASS_NAME;
    }

  }

  public Class<Items> getItems() {
    return Items.class;
  }

  public static class Items {

    public static String name() {
      return getName();
    }

    public static String getName() {
      return WebModelAttributes.ITEMS_NAME;
    }

  }

  public Class<Page> getPage() {
    return Page.class;
  }

  public static class Page {

    public static String name() {
      return getName();
    }

    public static String getName() {
      return WebModelAttributes.PAGE_NAME;
    }

  }

  public Class<Route> getRoute() {
    return Route.class;
  }

  public static class Route {

    public static String name() {
      return getName();
    }

    public static String getName() {
      return WebModelAttributes.ROUTE_NAME;
    }

  }

  public Class<TemplateRoute> getTemplateRoute() {
    return TemplateRoute.class;
  }

  public static class TemplateRoute {

    public static String name() {
      return getnName();
    }

    public static String getnName() {
      return WebModelAttributes.TEMPLATE_NAME;
    }

  }

  public Class<Parent> getParent() {
    return Parent.class;
  }

  public static class Parent {

    public static String name() {
      return getName();
    }

    public static String getName() {
      return WebModelAttributes.PARENT_NAME;
    }

  }

  public Class<ParentClass> getParentClass() {
    return ParentClass.class;
  }

  public static class ParentClass {

    public static String name() {
      return getName();
    }

    public static String getName() {
      return WebModelAttributes.PARENT_CLASS_NAME;
    }

  }

  public Class<Child> getChild() {
    return Child.class;
  }

  public static class Child {

    public static String name() {
      return getName();
    }

    public static String getName() {
      return WebModelAttributes.CHILD_NAME;
    }

  }

  public Class<ChildClass> getChildClass() {
    return ChildClass.class;
  }

  public static class ChildClass {

    public static String name() {
      return getName();
    }

    public static String getName() {
      return WebModelAttributes.CHILD_CLASS_NAME;
    }

  }

  public Class<Children> getChildren() {
    return Children.class;
  }

  public static class Children {

    public static String name() {
      return getName();
    }

    public static String getName() {
      return WebModelAttributes.CHILDREN_NAME;
    }

  }

  public Class<QueryConfig> getQueryConfig() {
    return QueryConfig.class;
  }

  public static class QueryConfig {

    public static String name() {
      return getName();
    }

    public static String getName() {
      return WebModelAttributes.QUERY_CONFIG_NAME;
    }

  }

  public Class<Messages> getMessages() {
    return Messages.class;
  }

  public static class Messages {

    public static String name() {
      return getName();
    }

    public static String getName() {
      return WebModelAttributes.MESSAGES_NAME;
    }

  }

  public Class<AjaxTargetId> getAjaxTargetId() {
    return AjaxTargetId.class;
  }

  public static class AjaxTargetId {

    public static String name() {
      return getName();
    }

    public static String getName() {
      return "ajaxTargetId";
    }

    public static String value() {
      return getValue();
    }

    public static String getValue() {
      return WebModelAttributes.AJAX_TARGET_ID_VALUE;
    }

  }

  public Class<EmbeddedTargetId> getEmbeddedTargetId() {
    return EmbeddedTargetId.class;
  }

  public static class EmbeddedTargetId {

    public static String name() {
      return getName();
    }

    public static String getName() {
      return "embeddedTargetId";
    }

    public static String value() {
      return getValue();
    }

    public static String getValue() {
      return WebModelAttributes.EMBEDDED_TARGET_ID_VALUE;
    }

  }

  public Class<Alert> getAlert() {
    return Alert.class;
  }

  public static class Alert {

    public static String name() {
      return getName();
    }

    public static String getName() {
      return WebModelAttributes.ALERT_NAME;
    }

  }

}
