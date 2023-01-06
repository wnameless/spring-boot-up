/*
 *
 * Copyright 2022 Wei-Ming Wu
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package com.github.wnameless.spring.boot.up.jsf;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public interface JsonSchemaForm {

  default boolean isEditable() {
    return true;
  }

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
