package com.github.wnameless.spring.boot.up.jsf.model;

import java.time.Instant;
import java.util.Map;
import com.github.wnameless.spring.boot.up.jsf.JsfVersioning;
import com.github.wnameless.spring.boot.up.web.IdProvider;

public interface JsfSchema<ID> extends JsfVersioning, IdProvider<ID> {

  void setFormType(String formType);

  void setFormBranch(String formBranch);

  Instant getVersion();

  void setVersion(Instant version);

  Map<String, Object> getSchema();

  void setSchema(Map<String, Object> schema);

  Map<String, Object> getUiSchema();

  void setUiSchema(Map<String, Object> uiSchema);

}
