package com.github.wnameless.spring.boot.up.jsf;

import java.util.LinkedHashMap;
import java.util.Map;
import com.github.wnameless.spring.boot.up.permission.resource.AccessControlRule;
import com.github.wnameless.spring.boot.up.permission.resource.AccessControllable;
import com.github.wnameless.spring.boot.up.web.RestfulItem;
import lombok.Data;

@Data
public class RestfulVersioningJsonSchemaForm<ID>
    implements JsonSchemaForm, JsfVersioning, RestfulItem<ID>, AccessControllable {

  private ID id;

  private String formType;
  private String formBranch;

  private Map<String, Object> formData = new LinkedHashMap<>();
  private Map<String, Object> schema = new LinkedHashMap<>();
  private Map<String, Object> uiSchema = new LinkedHashMap<>();

  private String basePath;
  private String indexPath;
  private String backPathname;

  AccessControlRule manageable = new AccessControlRule(false, () -> true);
  AccessControlRule crudable = new AccessControlRule(false, () -> true);
  AccessControlRule readable = new AccessControlRule(false, () -> true);
  AccessControlRule updatable = new AccessControlRule(false, () -> true);
  AccessControlRule deletable = new AccessControlRule(false, () -> true);

  public RestfulVersioningJsonSchemaForm(String formType, String formBranch, String basePath,
      ID id) {
    this.formType = formType;
    this.formBranch = formBranch;
    this.basePath = basePath;
    this.id = id;
    indexPath = basePath;
  }

  public RestfulVersioningJsonSchemaForm(String formType, String formBranch, String basePath, ID id,
      String indexPath) {
    this.formType = formType;
    this.formBranch = formBranch;
    this.basePath = basePath;
    this.id = id;
    this.indexPath = indexPath;
  }

  @Override
  public Map<String, Object> getFormData() {
    return formData;
  }

  @Override
  public void setFormData(Map<String, Object> formData) {
    this.formData = formData;
  }

  @Override
  public Map<String, Object> getSchema() {
    return schema;
  }

  @Override
  public void setSchema(Map<String, Object> schema) {
    this.schema = schema;
  }

  @Override
  public Map<String, Object> getUiSchema() {
    return uiSchema;
  }

  @Override
  public void setUiSchema(Map<String, Object> uiSchema) {
    this.uiSchema = uiSchema;
  }

}
