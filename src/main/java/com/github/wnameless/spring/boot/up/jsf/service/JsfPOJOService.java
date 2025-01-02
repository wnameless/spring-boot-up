package com.github.wnameless.spring.boot.up.jsf.service;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import com.github.wnameless.spring.boot.up.jsf.JsfConfig;
import com.github.wnameless.spring.boot.up.jsf.JsonCoreFactory;
import com.github.wnameless.spring.boot.up.jsf.JsonSchemaFormUtils;
import com.google.common.io.Resources;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public final class JsfPOJOService {

  private static final String DEFAULT_SCHEMA_BRANCH = "default";

  // Cache needs defensive copy
  private final Map<String, Map<String, Object>> schemaCache = new HashMap<>();

  public Map<String, Object> getSchemaTemplate(String formType, Locale locale) {
    return getSchemaTemplate(formType, locale.toString());
  }

  public Map<String, Object> getSchemaTemplate(String formType, String branch) {
    String templatePath = JsfConfig.getTemplatePath() + "/" + formType + "/" + formType + ".schema."
        + branch + ".json";
    if (schemaCache.containsKey(templatePath)) {
      return new LinkedHashMap<>(schemaCache.get(templatePath));
    }
    try {
      return readTemplate(templatePath);
    } catch (Exception e) {}

    templatePath = JsfConfig.getTemplatePath() + "/" + formType + "/" + formType + ".schema."
        + DEFAULT_SCHEMA_BRANCH + ".json";
    if (schemaCache.containsKey(templatePath)) {
      return new LinkedHashMap<>(schemaCache.get(templatePath));
    }
    try {
      return readTemplate(templatePath);
    } catch (Exception e) {
      return null;
    }
  }

  public Map<String, Object> getSchemaTemplate(String formType) {
    Locale locale = LocaleContextHolder.getLocale();
    Map<String, Object> template = getSchemaTemplate(formType, locale);
    if (template != null) {
      return template;
    }

    String templatePath =
        JsfConfig.getTemplatePath() + "/" + formType + "/" + formType + ".schema.json";
    if (schemaCache.containsKey(templatePath)) {
      return new LinkedHashMap<>(schemaCache.get(templatePath));
    }

    try {
      return readTemplate(templatePath);
    } catch (Exception e) {
      log.info("Schema template not found", e);
      return JsonSchemaFormUtils.defaultSchema();
    }
  }

  public Map<String, Object> getUiSchemaTemplate(String formType, Locale locale) {
    return getUiSchemaTemplate(formType, locale.toString());
  }

  public Map<String, Object> getUiSchemaTemplate(String formType, String branch) {
    String templatePath = JsfConfig.getTemplatePath() + "/" + formType + "/" + formType
        + ".uiSchema." + branch + ".json";
    if (schemaCache.containsKey(templatePath)) {
      return new LinkedHashMap<>(schemaCache.get(templatePath));
    }
    try {
      return readTemplate(templatePath);
    } catch (Exception e) {}

    templatePath = JsfConfig.getTemplatePath() + "/" + formType + "/" + formType + ".uiSchema."
        + DEFAULT_SCHEMA_BRANCH + ".json";
    if (schemaCache.containsKey(templatePath)) {
      return new LinkedHashMap<>(schemaCache.get(templatePath));
    }
    try {
      return readTemplate(templatePath);
    } catch (Exception e) {
      return null;
    }
  }

  public Map<String, Object> getUiSchemaTemplate(String formType) {
    Locale locale = LocaleContextHolder.getLocale();
    Map<String, Object> template = getUiSchemaTemplate(formType, locale);
    if (template != null) {
      return template;
    }

    String templatePath =
        JsfConfig.getTemplatePath() + "/" + formType + "/" + formType + ".uiSchema.json";
    if (schemaCache.containsKey(templatePath)) {
      return new LinkedHashMap<>(schemaCache.get(templatePath));
    }

    try {
      return readTemplate(templatePath);
    } catch (Exception e) {
      log.info("UiSchema template not found", e);
      return JsonSchemaFormUtils.defaultUiSchema();
    }
  }

  private LinkedHashMap<String, Object> readTemplate(String templatePath) throws Exception {
    URL schemaUrl = Resources.getResource(templatePath);
    String json = Resources.toString(schemaUrl, StandardCharsets.UTF_8);
    Map<String, Object> template = JsonCoreFactory.INSTANCE.readJson(json).asObject().toMap();
    schemaCache.put(templatePath, template);
    return new LinkedHashMap<>(template);
  }

}
