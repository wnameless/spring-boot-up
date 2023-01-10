package com.github.wnameless.spring.boot.up.jsf;

import java.util.LinkedHashMap;
import java.util.Map;
import com.github.wnameless.spring.boot.up.permission.resource.AccessControlAware;
import com.github.wnameless.spring.boot.up.permission.resource.AccessControlRule;
import com.github.wnameless.spring.boot.up.web.RestfulItem;
import lombok.Data;

@Data
public class RestfulJsonSchemaForm<ID>
    implements JsonSchemaForm, RestfulItem<ID>, AccessControlAware {

  private ID id;

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

  public RestfulJsonSchemaForm(String basePath, ID id) {
    this.basePath = basePath;
    this.id = id;
    indexPath = basePath;
  }

  public RestfulJsonSchemaForm(String basePath, ID id, String indexPath) {
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
