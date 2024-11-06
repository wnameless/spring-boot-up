package com.github.wnameless.spring.boot.up.tagging;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.jsf.RestfulJsonSchemaForm;
import com.github.wnameless.spring.boot.up.permission.PermittedUser;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.Alert;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.Item;
import com.github.wnameless.spring.boot.up.web.RestfulItemProvider;
import com.github.wnameless.spring.boot.up.web.RestfulRouteProvider;
import com.github.wnameless.spring.boot.up.web.WebActionAlertHelper.AlertMessages;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;
import lombok.SneakyThrows;

public interface TaggableController<E extends Taggable<T, UL, L, ID>, TS extends TaggingService<T, UL, L, ID>, T extends TagTemplate<UL, L, ID>, UL extends UserLabelTemplate<ID>, L extends LabelTemplate<ID>, ID>
    extends RestfulRouteProvider<ID>, RestfulItemProvider<E> {

  default String getFragmentName() {
    return "bs5";
  }

  default E getTaggable(ID id) {
    return getRestfulItem();
  }

  @SuppressWarnings("unchecked")
  default TS getTaggingService() {
    return (TS) SpringBootUp.getBean(TaggingService.class);
  }

  @SneakyThrows
  default RestfulJsonSchemaForm<?> createEditForm(E taggable, ID id) {
    var editform = new RestfulJsonSchemaForm<String>(getRestfulRoute().getShowPath(id), "taggings");
    var mapper = SpringBootUp.getBean(ObjectMapper.class);

    String schema = """
        {
          "title": "註記標籤",
          "type": "object",
          "required": [],
          "properties": {
            "labelList": {
              "type": "array",
              "title": "公用標籤",
              "items": {
                "type": "string"
              },
              "uniqueItems": true
            },
            "userLabelList": {
              "type": "array",
              "title": "私人標籤",
              "items": {
                "type": "string"
              },
              "uniqueItems": true
            },
            "systemLabelList": {
              "type": "array",
              "title": "系統標籤",
              "items": {
                "type": "string"
              },
              "uniqueItems": true
            }
          }
        }
            """;
    var schemaMap = mapper.readValue(schema, new TypeReference<Map<String, Object>>() {});
    DocumentContext docCtx = JsonPath.parse(schemaMap);

    var labelList = taggable.getLabelTemplates().stream() //
        .filter(LabelTemplate::isUserEditable).toList();
    if (!labelList.isEmpty()) {
      var labelListEnum = labelList.stream().map(LabelTemplate::getId).toList();
      docCtx.put("$.properties.labelList.items", "enum", labelListEnum);
      var labelListEnumNames = labelList.stream()
          .map(lt -> "[" + lt.getGroupTitle() + "] " + lt.getLabelName()).toList();
      docCtx.put("$.properties.labelList.items", "enumNames", labelListEnumNames);
    }

    var userLabelList = taggable.getUserLabelTemplates();
    if (!userLabelList.isEmpty()) {
      var userLabelListEnum = userLabelList.stream().map(UserLabelTemplate::getId).toList();
      docCtx.put("$.properties.userLabelList.items", "enum", userLabelListEnum);
      var userLabelListEnumNames = userLabelList.stream()
          .map(ult -> "[" + ult.getGroupTitle() + "] " + ult.getLabelName()).toList();
      docCtx.put("$.properties.userLabelList.items", "enumNames", userLabelListEnumNames);
    }

    var systemLabelList = taggable.getSystemLabels().stream() //
        .filter(LabelTemplate::isUserEditable) //
        .filter(l -> l.userPermissionStock() == null || l.userPermissionStock().getAsBoolean())
        .toList();
    if (!systemLabelList.isEmpty()) {
      var systemLabelListEnum = systemLabelList.stream().map(SystemLabel::getId).toList();
      docCtx.put("$.properties.systemLabelList.items", "enum", systemLabelListEnum);
      var systemLabelListEnumNames = systemLabelList.stream()
          .map(sl -> "[" + sl.getGroupTitle() + "] " + sl.getLabelName()).toList();
      docCtx.put("$.properties.systemLabelList.items", "enumNames", systemLabelListEnumNames);
    }

    schemaMap = docCtx.read("$", new TypeRef<Map<String, Object>>() {});
    editform.setSchema(schemaMap);

    String uiSchema = """
        {
          "labelList": {
            "ui:widget": "checkboxes"
          },
          "userLabelList": {
            "ui:widget": "checkboxes"
          },
          "systemLabelList": {
            "ui:widget": "checkboxes"
          }
        }
             """;
    editform.setUiSchema(mapper.readValue(uiSchema, new TypeReference<Map<String, Object>>() {}));

    var tags = taggable.getTagTemplates();
    String formDataSchema = """
        {
          "labelList": [],
          "userLabelList": [],
          "systemLabelList": []
        }
            """;
    docCtx = JsonPath.parse(formDataSchema);
    docCtx.put("$", "labelList", tags.stream().filter(tag -> tag.getLabelTemplate() != null)
        .map(l -> l.getLabelTemplate().getId()).toList());
    docCtx.put("$", "userLabelList", tags.stream().filter(tag -> tag.getUserLabelTemplate() != null)
        .map(l -> l.getUserLabelTemplate().getId()).toList());
    docCtx.put("$", "systemLabelList", tags.stream().filter(tag -> tag.getSystemLabel() != null)
        .map(l -> l.getSystemLabel().getId()).toList());
    editform.setFormData(docCtx.read("$", new TypeRef<Map<String, Object>>() {}));

    return editform;
  }

  @GetMapping("/{id}/taggings/edit")
  default ModelAndView editTaggings(ModelAndView mav, @PathVariable ID id) {
    mav.setViewName("sbu/taggings/edit :: " + getFragmentName());

    mav.addObject(Item.name(), createEditForm(getRestfulItem(), id));
    return mav;
  }

  @PutMapping("/{id}/taggings")
  default ModelAndView updateTaggings(ModelAndView mav, @PathVariable ID id,
      @RequestBody TaggableSchemaData<ID> data) {
    mav.setViewName("sbu/taggings/show :: " + getFragmentName());
    var taggable = getRestfulItem();
    if (!taggable.isTagEditable()) {
      var alertMessages = new AlertMessages();
      alertMessages.getWarning().add("Tags can NOT be modified");
      mav.addObject(Alert.name(), alertMessages);
      mav.addObject(Item.name(), createEditForm(getRestfulItem(), id));
      return mav;
    }

    taggable.getTagTemplates().stream().filter(tag -> {
      if (tag.getLabelTemplate() == null) return true;
      return tag.getLabelTemplate().isUserEditable();
    }).forEach(tag -> getTaggingService().getTagTemplateRepository().delete(tag));

    for (var labelId : data.getLabelList()) {
      var tag = getTaggingService().newTagTemplate();
      tag.setEntityId(id);
      var label = getTaggingService().getLabelTemplateRepository().findById(labelId);
      if (label.isEmpty()) continue;
      tag.setLabelTemplate(label.get());
      tag.setUsername(SpringBootUp.getBean(PermittedUser.class).getUsername());
      getTaggingService().getTagTemplateRepository().save(tag);
    }
    for (var userLabelId : data.getUserLabelList()) {
      var tag = getTaggingService().newTagTemplate();
      tag.setEntityId(id);
      var userLabel = getTaggingService().getUserLabelTemplateRepository().findById(userLabelId);
      if (userLabel.isEmpty()) continue;
      tag.setUserLabelTemplate(userLabel.get());
      tag.setUsername(SpringBootUp.getBean(PermittedUser.class).getUsername());
      getTaggingService().getTagTemplateRepository().save(tag);
    }
    for (var sysLabelId : data.getSystemLabelList()) {
      var tag = getTaggingService().newTagTemplate();
      tag.setEntityId(id);
      var sysLabel = getTaggingService().findSystemLabelById(sysLabelId);
      if (sysLabel.isEmpty()) continue;
      tag.setSystemLabel(sysLabel.get());
      tag.setUsername(SpringBootUp.getBean(PermittedUser.class).getUsername());
      getTaggingService().getTagTemplateRepository().save(tag);
    }

    mav.addObject(Item.name(), createEditForm(getRestfulItem(), id));
    return mav;
  }

}
