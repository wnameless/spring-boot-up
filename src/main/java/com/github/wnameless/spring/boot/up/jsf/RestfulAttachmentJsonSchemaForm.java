package com.github.wnameless.spring.boot.up.jsf;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.github.wnameless.spring.boot.up.attachment.Attachment;
import com.github.wnameless.spring.boot.up.attachment.AttachmentChecklist;
import com.github.wnameless.spring.boot.up.attachment.BasicAttachmentSnapshot;
import com.github.wnameless.spring.boot.up.attachment.StatelessAttachmentSnapshotProvider;
import lombok.Data;

@Data
public class RestfulAttachmentJsonSchemaForm<A extends Attachment<ID>, ID>
    implements JsonSchemaForm, StatelessAttachmentSnapshotProvider<A, ID> {

  private ID id;

  private Map<String, Object> formData = new LinkedHashMap<>();
  private Map<String, Object> schema = new LinkedHashMap<>();
  private Map<String, Object> uiSchema = new LinkedHashMap<>();

  private String basePath;
  private String indexPath;
  private String backPathname;

  private BasicAttachmentSnapshot<A, ID> attachmentSnapshot = new BasicAttachmentSnapshot<>();
  private AttachmentChecklist attachmentChecklist = new AttachmentChecklist();

  public RestfulAttachmentJsonSchemaForm() {}

  public RestfulAttachmentJsonSchemaForm(String basePath, ID id) {
    this.basePath = basePath;
    this.id = id;
    indexPath = basePath;
  }

  public RestfulAttachmentJsonSchemaForm(String basePath, ID id, String indexPath) {
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

  public RestfulAttachmentJsonSchemaForm<Attachment<ID>, ID> deepCopy() {
    RestfulAttachmentJsonSchemaForm<Attachment<ID>, ID> copy =
        new RestfulAttachmentJsonSchemaForm<>();
    copy.setId(this.getId());
    copy.setFormData(new LinkedHashMap<>(this.getFormData()));
    copy.setSchema(new LinkedHashMap<>(this.getSchema()));
    copy.setUiSchema(new LinkedHashMap<>(this.getUiSchema()));
    copy.setBasePath(this.getBasePath());
    copy.setIndexPath(this.getIndexPath());
    copy.setBackPathname(this.getBackPathname());

    // Deep copy attachment snapshot with cloned attachments
    if (this.getAttachmentSnapshot() != null) {
      copy.setAttachmentSnapshot(deepCopyAttachmentSnapshot(this.getAttachmentSnapshot()));
    }

    // AttachmentChecklist can be shared as it's not modified
    if (this.getAttachmentChecklist() != null) {
      copy.setAttachmentChecklist(this.getAttachmentChecklist());
    }
    return copy;
  }

  private BasicAttachmentSnapshot<Attachment<ID>, ID> deepCopyAttachmentSnapshot(
      BasicAttachmentSnapshot<A, ID> original) {
    if (original == null) {
      return null;
    }

    BasicAttachmentSnapshot<Attachment<ID>, ID> copy = new BasicAttachmentSnapshot<>();
    List<Attachment<ID>> attachments = new ArrayList<>();

    if (original.getAttachments() != null) {
      for (A attachment : original.getAttachments()) {
        attachments.add(deepCopyAttachment(attachment));
      }
    }

    copy.setAttachments(attachments);
    return copy;
  }

  private Attachment<ID> deepCopyAttachment(Attachment<ID> original) {
    if (original == null) {
      return null;
    }

    // Create a simple attachment copy
    // We'll use a basic implementation that preserves all properties
    return new Attachment<ID>() {
      private ID id = original.getId();
      private String group = original.getGroup();
      private String name = original.getName();
      private URI uri = original.getUri();
      private String note = original.getNote();
      private Instant createdAt = original.getCreatedAt();
      private String uiClassNames = original.getUiClassNames();

      @Override
      public ID getId() {
        return id;
      }

      @Override
      public void setId(ID id) {
        this.id = id;
      }

      @Override
      public String getGroup() {
        return group;
      }

      @Override
      public void setGroup(String group) {
        this.group = group;
      }

      @Override
      public String getName() {
        return name;
      }

      @Override
      public void setName(String name) {
        this.name = name;
      }

      @Override
      public URI getUri() {
        return uri;
      }

      @Override
      public void setUri(URI uri) {
        this.uri = uri;
      }

      @Override
      public String getNote() {
        return note;
      }

      @Override
      public void setNote(String note) {
        this.note = note;
      }

      @Override
      public Instant getCreatedAt() {
        return createdAt;
      }

      @Override
      public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
      }

      @Override
      public String getUiClassNames() {
        return uiClassNames;
      }

      @Override
      public void setUiClassNames(String classNames) {
        this.uiClassNames = classNames;
      }
    };
  }

}
