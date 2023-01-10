package com.github.wnameless.spring.boot.up.jsf;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.data.annotation.Transient;
import com.github.wnameless.spring.boot.up.permission.resource.AbstractAccessControlAware;
import com.github.wnameless.spring.boot.up.web.RestfulItem;
import lombok.Data;

@Data
public class RestfulJsonSchemaForm<ID> extends AbstractAccessControlAware
    implements JsonSchemaForm, RestfulItem<ID> {

  private ID id;

  private String basePath;

  private String indexPath;

  @Transient
  private String backPathname;

  private Map<String, Object> formData = new LinkedHashMap<>();

  private Map<String, Object> schema = new LinkedHashMap<>();

  private Map<String, Object> uiSchema = new LinkedHashMap<>();

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
