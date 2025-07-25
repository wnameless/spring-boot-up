package com.github.wnameless.spring.boot.up.jsf;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;

public interface JsonPathWebActionJsfPatch<D, ID> extends WebActionJsfPatch<D, ID> {

  default Function<? super JsonPathJsonSchemaForm, ? extends JsonPathJsonSchemaForm> jsonPathIndexActionWholePatch() {
    return null;
  }

  @Override
  default Function<? super JsonSchemaForm, ? extends JsonSchemaForm> indexActionWholePatch() {
    if (jsonPathIndexActionWholePatch() == null) return null;

    return jsf -> {
      var jsonPathJsf = new JsonPathJsonSchemaForm();
      jsonPathJsf.setSchema(jsf.getSchema());
      jsonPathJsf.setUiSchema(jsf.getUiSchema());
      jsonPathJsf.setFormData(jsf.getFormData());
      return jsonPathIndexActionWholePatch().apply(jsonPathJsf);
    };
  }

  default BiFunction<? super JsonSchemaForm, DocumentContext, DocumentContext> jsonPathIndexActionSchemaPatch() {
    return null;
  }

  @Override
  default Function<? super JsonSchemaForm, Map<String, Object>> indexActionSchemaPatch() {
    if (jsonPathIndexActionSchemaPatch() == null) return null;

    return jsf -> {
      DocumentContext docCtx = JsonPath.parse(jsf.getSchema());
      return jsonPathIndexActionSchemaPatch().apply(jsf, docCtx).read("$",
          new TypeRef<Map<String, Object>>() {});
    };
  }

  default BiFunction<? super JsonSchemaForm, DocumentContext, DocumentContext> jsonPathIndexActionUiSchemaPatch() {
    return null;
  }

  @Override
  default Function<? super JsonSchemaForm, Map<String, Object>> indexActionUiSchemaPatch() {
    if (jsonPathIndexActionUiSchemaPatch() == null) return null;

    return jsf -> {
      DocumentContext docCtx = JsonPath.parse(jsf.getUiSchema());
      return jsonPathIndexActionUiSchemaPatch().apply(jsf, docCtx).read("$",
          new TypeRef<Map<String, Object>>() {});
    };
  }

  default BiFunction<? super JsonSchemaForm, DocumentContext, DocumentContext> jsonPathIndexActionFormDataPatch() {
    return null;
  }

  @Override
  default Function<? super JsonSchemaForm, Map<String, Object>> indexActionFormDataPatch() {
    if (jsonPathIndexActionFormDataPatch() == null) return null;

    return jsf -> {
      DocumentContext docCtx = JsonPath.parse(jsf.getFormData());
      return jsonPathIndexActionFormDataPatch().apply(jsf, docCtx).read("$",
          new TypeRef<Map<String, Object>>() {});
    };
  }

  default Function<? super JsonPathJsonSchemaForm, ? extends JsonPathJsonSchemaForm> jsonPathShowActionWholePatch() {
    return null;
  }

  @Override
  default Function<? super JsonSchemaForm, ? extends JsonSchemaForm> showActionWholePatch() {
    if (jsonPathShowActionWholePatch() == null) return null;

    return jsf -> {
      var jsonPathJsf = new JsonPathJsonSchemaForm();
      jsonPathJsf.setSchema(jsf.getSchema());
      jsonPathJsf.setUiSchema(jsf.getUiSchema());
      jsonPathJsf.setFormData(jsf.getFormData());
      return jsonPathShowActionWholePatch().apply(jsonPathJsf);
    };
  }

  default BiFunction<? super JsonSchemaForm, DocumentContext, DocumentContext> jsonPathShowActionSchemaPatch() {
    return null;
  }

  @Override
  default Function<? super JsonSchemaForm, Map<String, Object>> showActionSchemaPatch() {
    if (jsonPathShowActionSchemaPatch() == null) return null;

    return jsf -> {
      DocumentContext docCtx = JsonPath.parse(jsf.getSchema());
      return jsonPathShowActionSchemaPatch().apply(jsf, docCtx).read("$",
          new TypeRef<Map<String, Object>>() {});
    };
  }

  default BiFunction<? super JsonSchemaForm, DocumentContext, DocumentContext> jsonPathShowActionUiSchemaPatch() {
    return null;
  }

  @Override
  default Function<? super JsonSchemaForm, Map<String, Object>> showActionUiSchemaPatch() {
    if (jsonPathShowActionUiSchemaPatch() == null) return null;

    return jsf -> {
      DocumentContext docCtx = JsonPath.parse(jsf.getUiSchema());
      return jsonPathShowActionUiSchemaPatch().apply(jsf, docCtx).read("$",
          new TypeRef<Map<String, Object>>() {});
    };
  }

  default BiFunction<? super JsonSchemaForm, DocumentContext, DocumentContext> jsonPathShowActionFormDataPatch() {
    return null;
  }

  @Override
  default Function<? super JsonSchemaForm, Map<String, Object>> showActionFormDataPatch() {
    if (jsonPathShowActionFormDataPatch() == null) return null;

    return jsf -> {
      DocumentContext docCtx = JsonPath.parse(jsf.getFormData());
      return jsonPathShowActionFormDataPatch().apply(jsf, docCtx).read("$",
          new TypeRef<Map<String, Object>>() {});
    };
  }

  default Function<? super JsonPathJsonSchemaForm, ? extends JsonPathJsonSchemaForm> jsonPathNewActionWholePatch() {
    return null;
  }

  @Override
  default Function<? super JsonSchemaForm, ? extends JsonSchemaForm> newActionWholePatch() {
    if (jsonPathNewActionWholePatch() == null) return null;

    return jsf -> {
      var jsonPathJsf = new JsonPathJsonSchemaForm();
      jsonPathJsf.setSchema(jsf.getSchema());
      jsonPathJsf.setUiSchema(jsf.getUiSchema());
      jsonPathJsf.setFormData(jsf.getFormData());
      return jsonPathNewActionWholePatch().apply(jsonPathJsf);
    };
  }

  default BiFunction<? super JsonSchemaForm, DocumentContext, DocumentContext> jsonPathNewActionSchemaPatch() {
    return null;
  }

  @Override
  default Function<? super JsonSchemaForm, Map<String, Object>> newActionSchemaPatch() {
    if (jsonPathNewActionSchemaPatch() == null) return null;

    return jsf -> {
      DocumentContext docCtx = JsonPath.parse(jsf.getSchema());
      return jsonPathNewActionSchemaPatch().apply(jsf, docCtx).read("$",
          new TypeRef<Map<String, Object>>() {});
    };
  }

  default BiFunction<? super JsonSchemaForm, DocumentContext, DocumentContext> jsonPathNewActionUiSchemaPatch() {
    return null;
  }

  @Override
  default Function<? super JsonSchemaForm, Map<String, Object>> newActionUiSchemaPatch() {
    if (jsonPathNewActionUiSchemaPatch() == null) return null;

    return jsf -> {
      DocumentContext docCtx = JsonPath.parse(jsf.getUiSchema());
      return jsonPathNewActionUiSchemaPatch().apply(jsf, docCtx).read("$",
          new TypeRef<Map<String, Object>>() {});
    };
  }

  default BiFunction<? super JsonSchemaForm, DocumentContext, DocumentContext> jsonPathNewActionFormDataPatch() {
    return null;
  }

  @Override
  default Function<? super JsonSchemaForm, Map<String, Object>> newActionFormDataPatch() {
    if (jsonPathNewActionFormDataPatch() == null) return null;

    return jsf -> {
      DocumentContext docCtx = JsonPath.parse(jsf.getFormData());
      return jsonPathNewActionFormDataPatch().apply(jsf, docCtx).read("$",
          new TypeRef<Map<String, Object>>() {});
    };
  }

  default Function<? super JsonPathJsonSchemaForm, ? extends JsonPathJsonSchemaForm> jsonPathCreateActionWholePatch() {
    return null;
  }

  @Override
  default Function<? super JsonSchemaForm, ? extends JsonSchemaForm> createActionWholePatch() {
    if (jsonPathCreateActionWholePatch() == null) return null;

    return jsf -> {
      var jsonPathJsf = new JsonPathJsonSchemaForm();
      jsonPathJsf.setSchema(jsf.getSchema());
      jsonPathJsf.setUiSchema(jsf.getUiSchema());
      jsonPathJsf.setFormData(jsf.getFormData());
      return jsonPathCreateActionWholePatch().apply(jsonPathJsf);
    };
  }

  default BiFunction<? super JsonSchemaForm, DocumentContext, DocumentContext> jsonPathCreateActionSchemaPatch() {
    return null;
  }

  @Override
  default Function<? super JsonSchemaForm, Map<String, Object>> createActionSchemaPatch() {
    if (jsonPathCreateActionSchemaPatch() == null) return null;

    return jsf -> {
      DocumentContext docCtx = JsonPath.parse(jsf.getSchema());
      return jsonPathCreateActionSchemaPatch().apply(jsf, docCtx).read("$",
          new TypeRef<Map<String, Object>>() {});
    };
  }

  default BiFunction<? super JsonSchemaForm, DocumentContext, DocumentContext> jsonPathCreateActionUiSchemaPatch() {
    return null;
  }

  @Override
  default Function<? super JsonSchemaForm, Map<String, Object>> createActionUiSchemaPatch() {
    if (jsonPathCreateActionUiSchemaPatch() == null) return null;

    return jsf -> {
      DocumentContext docCtx = JsonPath.parse(jsf.getUiSchema());
      return jsonPathCreateActionUiSchemaPatch().apply(jsf, docCtx).read("$",
          new TypeRef<Map<String, Object>>() {});
    };
  }

  default BiFunction<? super JsonSchemaForm, DocumentContext, DocumentContext> jsonPathCreateActionFormDataPatch() {
    return null;
  }

  @Override
  default Function<? super JsonSchemaForm, Map<String, Object>> createActionFormDataPatch() {
    if (jsonPathCreateActionFormDataPatch() == null) return null;

    return jsf -> {
      DocumentContext docCtx = JsonPath.parse(jsf.getFormData());
      return jsonPathCreateActionFormDataPatch().apply(jsf, docCtx).read("$",
          new TypeRef<Map<String, Object>>() {});
    };
  }

  default Function<? super JsonPathJsonSchemaForm, ? extends JsonPathJsonSchemaForm> jsonPathEditActionWholePatch() {
    return null;
  }

  @Override
  default Function<? super JsonSchemaForm, ? extends JsonSchemaForm> editActionWholePatch() {
    if (jsonPathEditActionWholePatch() == null) return null;

    return jsf -> {
      var jsonPathJsf = new JsonPathJsonSchemaForm();
      jsonPathJsf.setSchema(jsf.getSchema());
      jsonPathJsf.setUiSchema(jsf.getUiSchema());
      jsonPathJsf.setFormData(jsf.getFormData());
      return jsonPathEditActionWholePatch().apply(jsonPathJsf);
    };
  }

  default BiFunction<? super JsonSchemaForm, DocumentContext, DocumentContext> jsonPathEditActionSchemaPatch() {
    return null;
  }

  @Override
  default Function<? super JsonSchemaForm, Map<String, Object>> editActionSchemaPatch() {
    if (jsonPathEditActionSchemaPatch() == null) return null;

    return jsf -> {
      DocumentContext docCtx = JsonPath.parse(jsf.getSchema());
      return jsonPathEditActionSchemaPatch().apply(jsf, docCtx).read("$",
          new TypeRef<Map<String, Object>>() {});
    };
  }

  default BiFunction<? super JsonSchemaForm, DocumentContext, DocumentContext> jsonPathEditActionUiSchemaPatch() {
    return null;
  }

  @Override
  default Function<? super JsonSchemaForm, Map<String, Object>> editActionUiSchemaPatch() {
    if (jsonPathEditActionUiSchemaPatch() == null) return null;

    return jsf -> {
      DocumentContext docCtx = JsonPath.parse(jsf.getUiSchema());
      return jsonPathEditActionUiSchemaPatch().apply(jsf, docCtx).read("$",
          new TypeRef<Map<String, Object>>() {});
    };
  }

  default BiFunction<? super JsonSchemaForm, DocumentContext, DocumentContext> jsonPathEditActionFormDataPatch() {
    return null;
  }

  @Override
  default Function<? super JsonSchemaForm, Map<String, Object>> editActionFormDataPatch() {
    if (jsonPathEditActionFormDataPatch() == null) return null;

    return jsf -> {
      DocumentContext docCtx = JsonPath.parse(jsf.getFormData());
      return jsonPathEditActionFormDataPatch().apply(jsf, docCtx).read("$",
          new TypeRef<Map<String, Object>>() {});
    };
  }

  default Function<? super JsonPathJsonSchemaForm, ? extends JsonPathJsonSchemaForm> jsonPathUpdateActionWholePatch() {
    return null;
  }

  @Override
  default Function<? super JsonSchemaForm, ? extends JsonSchemaForm> updateActionWholePatch() {
    if (jsonPathUpdateActionWholePatch() == null) return null;

    return jsf -> {
      var jsonPathJsf = new JsonPathJsonSchemaForm();
      jsonPathJsf.setSchema(jsf.getSchema());
      jsonPathJsf.setUiSchema(jsf.getUiSchema());
      jsonPathJsf.setFormData(jsf.getFormData());
      return jsonPathUpdateActionWholePatch().apply(jsonPathJsf);
    };
  }

  default BiFunction<? super JsonSchemaForm, DocumentContext, DocumentContext> jsonPathUpdateActionSchemaPatch() {
    return null;
  }

  @Override
  default Function<? super JsonSchemaForm, Map<String, Object>> updateActionSchemaPatch() {
    if (jsonPathUpdateActionSchemaPatch() == null) return null;

    return jsf -> {
      DocumentContext docCtx = JsonPath.parse(jsf.getSchema());
      return jsonPathUpdateActionSchemaPatch().apply(jsf, docCtx).read("$",
          new TypeRef<Map<String, Object>>() {});
    };
  }

  default BiFunction<? super JsonSchemaForm, DocumentContext, DocumentContext> jsonPathUpdateActionUiSchemaPatch() {
    return null;
  }

  @Override
  default Function<? super JsonSchemaForm, Map<String, Object>> updateActionUiSchemaPatch() {
    if (jsonPathUpdateActionUiSchemaPatch() == null) return null;

    return jsf -> {
      DocumentContext docCtx = JsonPath.parse(jsf.getUiSchema());
      return jsonPathUpdateActionUiSchemaPatch().apply(jsf, docCtx).read("$",
          new TypeRef<Map<String, Object>>() {});
    };
  }

  default BiFunction<? super JsonSchemaForm, DocumentContext, DocumentContext> jsonPathUpdateActionFormDataPatch() {
    return null;
  }

  @Override
  default Function<? super JsonSchemaForm, Map<String, Object>> updateActionFormDataPatch() {
    if (jsonPathUpdateActionFormDataPatch() == null) return null;

    return jsf -> {
      DocumentContext docCtx = JsonPath.parse(jsf.getFormData());
      return jsonPathUpdateActionFormDataPatch().apply(jsf, docCtx).read("$",
          new TypeRef<Map<String, Object>>() {});
    };
  }

  default Function<? super JsonPathJsonSchemaForm, ? extends JsonPathJsonSchemaForm> jsonPathDeleteActionWholePatch() {
    return null;
  }

  @Override
  default Function<? super JsonSchemaForm, ? extends JsonSchemaForm> deleteActionWholePatch() {
    if (jsonPathDeleteActionWholePatch() == null) return null;

    return jsf -> {
      var jsonPathJsf = new JsonPathJsonSchemaForm();
      jsonPathJsf.setSchema(jsf.getSchema());
      jsonPathJsf.setUiSchema(jsf.getUiSchema());
      jsonPathJsf.setFormData(jsf.getFormData());
      return jsonPathDeleteActionWholePatch().apply(jsonPathJsf);
    };
  }

  default BiFunction<? super JsonSchemaForm, DocumentContext, DocumentContext> jsonPathDeleteActionSchemaPatch() {
    return null;
  }

  @Override
  default Function<? super JsonSchemaForm, Map<String, Object>> deleteActionSchemaPatch() {
    if (jsonPathDeleteActionSchemaPatch() == null) return null;

    return jsf -> {
      DocumentContext docCtx = JsonPath.parse(jsf.getSchema());
      return jsonPathDeleteActionSchemaPatch().apply(jsf, docCtx).read("$",
          new TypeRef<Map<String, Object>>() {});
    };
  }

  default BiFunction<? super JsonSchemaForm, DocumentContext, DocumentContext> jsonPathDeleteActionUiSchemaPatch() {
    return null;
  }

  @Override
  default Function<? super JsonSchemaForm, Map<String, Object>> deleteActionUiSchemaPatch() {
    if (jsonPathDeleteActionUiSchemaPatch() == null) return null;

    return jsf -> {
      DocumentContext docCtx = JsonPath.parse(jsf.getUiSchema());
      return jsonPathDeleteActionUiSchemaPatch().apply(jsf, docCtx).read("$",
          new TypeRef<Map<String, Object>>() {});
    };
  }

  default BiFunction<? super JsonSchemaForm, DocumentContext, DocumentContext> jsonPathDeleteActionFormDataPatch() {
    return null;
  }

  @Override
  default Function<? super JsonSchemaForm, Map<String, Object>> deleteActionFormDataPatch() {
    if (jsonPathDeleteActionFormDataPatch() == null) return null;

    return jsf -> {
      DocumentContext docCtx = JsonPath.parse(jsf.getFormData());
      return jsonPathDeleteActionFormDataPatch().apply(jsf, docCtx).read("$",
          new TypeRef<Map<String, Object>>() {});
    };
  }

}
