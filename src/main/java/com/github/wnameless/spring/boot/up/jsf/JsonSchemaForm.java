package com.github.wnameless.spring.boot.up.jsf;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public interface JsonSchemaForm {

  Map<String, Object> getFormData();

  void setFormData(Map<String, Object> formData);

  Map<String, Object> getSchema();

  default void setSchema(Map<String, Object> schema) {}

  Map<String, Object> getUiSchema();

  default void setUiSchema(Map<String, Object> uiSchema) {}

  default Map<String, Object> toDataset() {
    Map<String, Object> dataset = new LinkedHashMap<>();

    dataset.put("formData", getFormData());
    dataset.put("schema", getSchema());
    dataset.put("uiSchema", getUiSchema());

    return dataset;
  }

  default Map<String, Object> toDataset(Map<String, Object> attrs) {
    Map<String, Object> dataset = toDataset();

    for (Entry<String, Object> attr : attrs.entrySet()) {
      dataset.put(attr.getKey(), attr.getValue());
    }

    return dataset;
  }

}
