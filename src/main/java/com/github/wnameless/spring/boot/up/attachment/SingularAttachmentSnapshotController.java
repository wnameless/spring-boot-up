package com.github.wnameless.spring.boot.up.attachment;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.jsf.RestfulJsonSchemaForm;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.AjaxTargetId;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.Item;
import com.github.wnameless.spring.boot.up.web.RestfulItem;
import com.github.wnameless.spring.boot.up.web.TemplateFragmentAware;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import net.sf.rubycollect4j.Ruby;

public interface SingularAttachmentSnapshotController<AA extends AttachmentSnapshotProvider<AA, A, ID> & RestfulItem<ID>, S extends AttachmentService<A, ID>, A extends Attachment<ID>, ID>
    extends TemplateFragmentAware {

  AA getAttachmentSnapshotProvider();

  @SuppressWarnings("unchecked")
  default S getAttachmentService() {
    return (S) SpringBootUp.getBean(AttachmentService.class);
  }

  default void updateSnapshot(AA attachmentSnapshotAware, List<A> attachments) {
    var original = attachmentSnapshotAware.getAttachmentSnapshot().getAttachments();
    if (original == null) original = new ArrayList<>();

    for (var a : attachments) {
      if (attachmentSnapshotAware.isValidAttachment(a)) {
        if (attachmentSnapshotAware.isExistedAttachment(a)) {
          var removedOne = attachmentSnapshotAware.removeExistedAttachment(a);
          if (removedOne != null) {
            if (getAttachmentService().outdatedAttachmentProcedure().isPresent()) {
              getAttachmentService().outdatedAttachmentProcedure().get()
                  .accept(Arrays.asList(removedOne));
            }
          }
        }
        original.add(a);
      }
    }

    attachmentSnapshotAware.getAttachmentSnapshot().setAttachments(original);
    attachmentSnapshotAware.saveAttachmentSnapshotProvider();;
  }

  @SneakyThrows
  default RestfulJsonSchemaForm<?> createEditForm(AttachmentChecklist checklist,
      AttachmentSnapshot<A, ID> snapshot) {
    var editform = new RestfulJsonSchemaForm<String>(getAttachmentSnapshotProvider().getShowPath(),
        "attachments");
    var mapper = SpringBootUp.getBean(ObjectMapper.class);
    var attachmentsGroups = snapshot.getAttachmentsByGroup();

    // editform.getSchema().put("title", "Files");
    editform.getSchema().put("type", "object");
    var aryProps = new LinkedHashMap<>();
    String ary = """
        {
          "type": "array",
          "items": {
            "type": "object",
            "title": "%s",
            "required": [
              "fileName"
            ],
            "properties": {
              "fileName": {
                "title": "%s",
                "type": "string"
              },
              "note": {
                "title": "%s",
                "type": "string",
                "default": ""
              }
            }
          }
        }
          """.formatted(AttachmentI18nHelper.getAttachmentTitle(),
        AttachmentI18nHelper.getFileName(), AttachmentI18nHelper.getNote());;
    for (String group : checklist.getGroupNames().stream()
        .filter(gn -> attachmentsGroups.keySet().contains(gn)).toList()) {
      aryProps.put(group, mapper.readValue(ary, Map.class));
    }
    editform.getSchema().put("properties", aryProps);

    String uiOpt = """
        {
          "ui:options": {
            "addable": false,
            "orderable": false,
            "removable": true
          }
        }
           """;
    for (String group : attachmentsGroups.keySet()) {
      editform.getUiSchema().put(group, mapper.readValue(uiOpt, Map.class));
    }

    for (String group : attachmentsGroups.keySet()) {
      editform.getFormData().put(group, attachmentsGroups.get(group).stream()
          .map(a -> Ruby.Hash.of("fileName", a.getName(), "note", a.getNote())).toList());
    }

    return editform;
  }

  @SneakyThrows
  default RestfulJsonSchemaForm<?> createNoteForm(AttachmentChecklist checklist,
      AttachmentSnapshot<A, ID> snapshot) {
    var editform = new RestfulJsonSchemaForm<String>(getAttachmentSnapshotProvider().getShowPath(),
        "attachments");
    var mapper = SpringBootUp.getBean(ObjectMapper.class);
    var attachmentsGroups = snapshot.getAttachmentsByGroup();

    // editform.getSchema().put("title", "Files");
    editform.getSchema().put("type", "object");
    var aryProps = new LinkedHashMap<>();
    String ary = """
        {
          "type": "array",
          "items": {
            "type": "object",
            "title": "%s",
            "required": [
              "fileName"
            ],
            "properties": {
              "fileName": {
                "title": "%s",
                "type": "string"
              },
              "note": {
                "title": "%s",
                "type": "string",
                "default": ""
              }
            }
          }
        }
          """.formatted(AttachmentI18nHelper.getAttachmentTitle(),
        AttachmentI18nHelper.getFileName(), AttachmentI18nHelper.getNote());
    for (String group : checklist.getGroupNames().stream()
        .filter(gn -> attachmentsGroups.keySet().contains(gn)).toList()) {
      aryProps.put(group, mapper.readValue(ary, Map.class));
    }
    editform.getSchema().put("properties", aryProps);

    String uiOpt = """
        {
          "items": {
            "fileName": {
              "ui:readonly": true
            }
          },
          "ui:options": {
            "addable": false,
            "orderable": false,
            "removable": false
          }
        }
           """;
    for (String group : attachmentsGroups.keySet()) {
      editform.getUiSchema().put(group, mapper.readValue(uiOpt, Map.class));
    }

    for (String group : attachmentsGroups.keySet()) {
      editform.getFormData().put(group, attachmentsGroups.get(group).stream()
          .map(a -> Ruby.Hash.of("fileName", a.getName(), "note", a.getNote())).toList());
    }

    return editform;
  }

  @SneakyThrows
  default RestfulJsonSchemaForm<?> createUploadSelectiveForm(AttachmentChecklist checklist) {
    var uploadform = new RestfulJsonSchemaForm<String>(
        getAttachmentSnapshotProvider().getShowPath() + "/attachments", "");
    var mapper = SpringBootUp.getBean(ObjectMapper.class);

    uploadform.getSchema().put("type", "object");
    uploadform.getSchema().put("properties", Map.of());

    var anyOfAry = new ArrayList<>();
    uploadform.getSchema().put("anyOf", anyOfAry);

    String single = """
        {
          "title": " ",
          "type": "string",
          "format": "data-url"
        }
          """;
    String mutiple = """
        {
          "title": " ",
          "type": "array",
          "items": {
            "type": "string",
            "format": "data-url"
          }
        }
          """;
    for (var ag : checklist.getAttachmentGroups()) {
      var anyOf = new LinkedHashMap<>();
      anyOf.put("title", ag.getGroup());
      if (ag.isSingle()) {
        var s = mapper.readValue(single, Map.class);
        anyOf.put("properties", Map.of(ag.getGroup(), s));
      } else {
        var m = mapper.readValue(mutiple, Map.class);
        anyOf.put("properties", Map.of(ag.getGroup(), m));
      }
      anyOfAry.add(anyOf);
    }

    return uploadform;
  }

  @SneakyThrows
  default RestfulJsonSchemaForm<?> createUploadForm(AttachmentChecklist checklist, ID id) {
    var uploadform = new RestfulJsonSchemaForm<String>(
        getAttachmentSnapshotProvider().getShowPath() + "/attachments", "");
    var mapper = SpringBootUp.getBean(ObjectMapper.class);

    // uploadform.getSchema().put("title", "Files");
    uploadform.getSchema().put("type", "object");
    var fileProps = new LinkedHashMap<>();
    String single = """
        {
          "type": "string",
          "format": "data-url"
        }
          """;
    String mutiple = """
        {
          "type": "array",
          "items": {
            "type": "string",
            "format": "data-url"
          }
        }
          """;
    for (var ag : checklist.getAttachmentGroups()) {
      if (ag.isSingle()) {
        fileProps.put(ag.getGroup(), mapper.readValue(single, Map.class));
      } else {
        fileProps.put(ag.getGroup(), mapper.readValue(mutiple, Map.class));
      }
    }
    uploadform.getSchema().put("properties", fileProps);

    return uploadform;
  }

  @GetMapping("/attachments")
  default ModelAndView retrieveAttachments(ModelAndView mav, @RequestParam String ajaxTargetId) {
    mav.setViewName("sbu/attachments/panel :: " + getFragmentName());

    var attachmentSnapshotProvider = getAttachmentSnapshotProvider();
    var snapshot = attachmentSnapshotProvider.getAttachmentSnapshot();
    var checklist = attachmentSnapshotProvider.getAttachmentChecklist();

    mav.addObject("attachmentChecklist", checklist);
    mav.addObject("attachmentGroups", snapshot.getAttachmentsByGroup());
    mav.addObject(AjaxTargetId.name(), ajaxTargetId);
    return mav;
  }

  @GetMapping("/attachments/edit")
  default ModelAndView editAttachments(ModelAndView mav, @RequestParam String ajaxTargetId) {
    mav.setViewName("sbu/attachments/edit :: " + getFragmentName());

    var attachmentSnapshotProvider = getAttachmentSnapshotProvider();
    var checklist = attachmentSnapshotProvider.getAttachmentChecklist();
    var snapshot = attachmentSnapshotProvider.getAttachmentSnapshot();

    mav.addObject(Item.name(), createEditForm(checklist, snapshot));
    mav.addObject(AjaxTargetId.name(), ajaxTargetId);
    return mav;
  }

  @GetMapping("/attachments/note")
  default ModelAndView noteAttachments(ModelAndView mav, @RequestParam String ajaxTargetId) {
    mav.setViewName("sbu/attachments/note :: " + getFragmentName());

    var attachmentSnapshotProvider = getAttachmentSnapshotProvider();
    var checklist = attachmentSnapshotProvider.getAttachmentChecklist();
    var snapshot = attachmentSnapshotProvider.getAttachmentSnapshot();

    mav.addObject(Item.name(), createNoteForm(checklist, snapshot));
    mav.addObject(AjaxTargetId.name(), ajaxTargetId);
    return mav;
  }

  @GetMapping("/attachments/upload")
  default ModelAndView uploadFragment(ModelAndView mav, @RequestParam String ajaxTargetId) {
    mav.setViewName("sbu/attachments/upload :: " + getFragmentName());

    var attachmentSnapshotProvider = getAttachmentSnapshotProvider();
    var checklist = attachmentSnapshotProvider.getAttachmentChecklist();

    mav.addObject(Item.name(), createUploadSelectiveForm(checklist));
    mav.addObject(AjaxTargetId.name(), ajaxTargetId);
    return mav;
  }

  @PostMapping("/attachments")
  default ModelAndView uploadAttachments(ModelAndView mav,
      @RequestBody Map<String, Object> jsfFiles, @RequestParam String ajaxTargetId) {
    mav.setViewName("sbu/attachments/panel :: " + getFragmentName());

    var attachmentSnapshotProvider = getAttachmentSnapshotProvider();
    var service = getAttachmentService();

    List<A> attachments = new ArrayList<>();
    jsfFiles.entrySet().forEach(pair -> {
      String group = pair.getKey();
      if (pair.getValue() instanceof Collection) {
        Collection<?> fs = (Collection<?>) pair.getValue();
        fs.stream().forEach(f -> {
          if (f instanceof String string) {
            var data = new Base64EncodingAttachmentableData(string);

            A a = getAttachmentService().newAttachment();
            a.setGroup(group);
            a.setName(data.getName());
            a.setCreatedAt(LocalDateTime.now());
            a.setUri(service.writeData(data.getBytes()));
            service.saveAttachment(a);

            attachments.add(a);
          }

        });
      } else if (pair.getValue() instanceof String) {
        var data = new Base64EncodingAttachmentableData((String) pair.getValue());

        A a = getAttachmentService().newAttachment();
        a.setGroup(group);
        a.setName(data.getName());
        a.setCreatedAt(LocalDateTime.now());
        a.setUri(service.writeData(data.getBytes()));
        service.saveAttachment(a);

        attachments.add(a);
      }
    });
    updateSnapshot(attachmentSnapshotProvider, attachments);

    mav.addObject("attachmentChecklist", attachmentSnapshotProvider.getAttachmentChecklist());
    mav.addObject("attachmentGroups",
        attachmentSnapshotProvider.getAttachmentSnapshot().getAttachmentsByGroup());
    mav.addObject(AjaxTargetId.name(), ajaxTargetId);
    return mav;
  }

  @GetMapping(path = "/attachments/{attachmentId}")
  default void downloadAttachment(HttpServletResponse response, @PathVariable ID attachmentId) {
    var attachmentSnapshot = getAttachmentSnapshotProvider().getAttachmentSnapshot();
    Optional<A> attachmentOpt = attachmentSnapshot.findAttachment(attachmentId);

    if (attachmentOpt.isEmpty()) return;

    A attachment = attachmentOpt.get();
    try (InputStream inputStream = getAttachmentService().readData(attachment)) {
      response.setContentType("application/octet-stream");
      response.setHeader("Content-Disposition", "attachment; filename="
          + URLEncoder.encode(attachment.getName(), StandardCharsets.UTF_8));

      int nRead;
      while ((nRead = inputStream.read()) != -1) {
        response.getWriter().write(nRead);
      }
    } catch (IOException e) {}
  }

  @PutMapping(path = "/attachments")
  default ModelAndView modifyAttachments(ModelAndView mav,
      @RequestBody Map<String, Object> jsfFiles, @RequestParam String ajaxTargetId) {
    mav.setViewName("sbu/attachments/panel :: " + getFragmentName());

    var attachmentSnapshotProvider = getAttachmentSnapshotProvider();
    var original = attachmentSnapshotProvider.getAttachmentSnapshot().getAttachments();
    var filtered = new ArrayList<A>();

    jsfFiles.entrySet().forEach(pair -> {
      String group = pair.getKey();
      if (pair.getValue() instanceof Collection) {
        Collection<?> nameAndNotes = (Collection<?>) pair.getValue();
        nameAndNotes.stream().forEach(nameAndNote -> {
          if (nameAndNote instanceof Map nan) {
            filtered.addAll(original.stream().filter(a -> Objects.equals(a.getGroup(), group)
                && Objects.equals(a.getName(), nan.get("fileName"))).map(a -> {
                  a.setNote((String) nan.get("note"));
                  return a;
                }).toList());
          }
        });
      } else if (pair.getValue() instanceof Map nan) {
        var name = nan.get("fileName");
        filtered.addAll(original.stream()
            .filter(a -> Objects.equals(a.getGroup(), group) && Objects.equals(a.getName(), name))
            .map(a -> {
              a.setNote((String) nan.get("note"));
              return a;
            }).toList());
      }
    });
    var oldAttachments = attachmentSnapshotProvider.getAttachmentSnapshot().getAttachments();
    attachmentSnapshotProvider.getAttachmentSnapshot().setAttachments(filtered);
    attachmentSnapshotProvider.saveAttachmentSnapshotProvider();
    if (getAttachmentService().outdatedAttachmentProcedure().isPresent()) {
      oldAttachments.removeAll(filtered);
      getAttachmentService().outdatedAttachmentProcedure().get().accept(oldAttachments);
    }

    mav.addObject("attachmentChecklist", attachmentSnapshotProvider.getAttachmentChecklist());
    mav.addObject("attachmentGroups",
        attachmentSnapshotProvider.getAttachmentSnapshot().getAttachmentsByGroup());
    mav.addObject(AjaxTargetId.name(), ajaxTargetId);
    return mav;
  }

}
