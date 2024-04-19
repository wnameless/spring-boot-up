package com.github.wnameless.spring.boot.up.tagging;

import java.util.Optional;
import java.util.function.BooleanSupplier;
import com.github.wnameless.spring.boot.up.web.IdProvider;

public interface LabelTemplate<ID> extends IdProvider<ID> {

  String getGroupTitle();

  void setGroupTitle(String groupTitle);

  String getLabelName();

  void setLabelName(String labelName);

  String getLabelColor();

  void setLabelColor(String code);

  String getEntityType();

  void setEntityType(String entityType);

  default void setEntityTypeByClass(Class<?> type) {
    setEntityType(type.getName());
  }

  default Optional<Class<?>> getEntityTypeByClass() {
    try {
      var klass = Class.forName(getEntityType());
      return Optional.of(klass);
    } catch (ClassNotFoundException e) {
      return Optional.empty();
    }
  }

  String getUsername();

  boolean isUserEditable();

  void setUserEditable(boolean userEditable);

  default BooleanSupplier userPermissionStock() {
    return null;
  }

  default void userPermissionStock(BooleanSupplier permissionStock) {}

}
